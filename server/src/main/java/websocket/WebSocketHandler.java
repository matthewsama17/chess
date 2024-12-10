package websocket;

import chess.*;
import chess.ChessGame.TeamColor;
import com.google.gson.*;
import dataaccess.*;
import dataaccess.sql.*;
import model.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private AuthDAO authDAO = new SQLAuthDAO();
    private GameDAO gameDAO = new SQLGameDAO();
    private UserDAO userDAO = new SQLUserDAO();

    private final ConnectionManager connections = new ConnectionManager();
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(UserGameCommand.class,
                    (JsonDeserializer<UserGameCommand>) (el, type, ctx) -> {
                        UserGameCommand userGameCommand = null;
                        if(el.isJsonObject()) {
                            String commandType = el.getAsJsonObject().get("commandType").getAsString();
                            if(UserGameCommand.CommandType.valueOf(commandType) == UserGameCommand.CommandType.MAKE_MOVE) {
                                userGameCommand = ctx.deserialize(el, MakeMoveCommand.class);
                            }
                            else {
                                userGameCommand = ctx.deserialize(el, NormalGameCommand.class);
                            }
                        }
                        return userGameCommand;
                    })
            .create();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

        String username = null;
        try {
            AuthData authData = authDAO.getAuth(command.getAuthToken());
            if(authData == null) {
                throw new Exception();
            }
            username = authData.username();
        }
        catch (Exception ex) {
            sendError(session, "ERROR: Your connection has timed out.");
        }

        GameData gameData = null;
        try {
            gameData = gameDAO.getGame(command.getGameID());
            if(gameData == null) {
                throw new Exception();
            }
        }
        catch (Exception ex) {
            sendError(session, "ERROR: Game could not be found.");
        }

        UserGameCommand.CommandType type = command.getCommandType();

        TeamColor color = null;
        if(username.equals(gameData.whiteUsername())) {
            color = TeamColor.WHITE;
        }
        else if(username.equals(gameData.blackUsername())) {
            color = TeamColor.BLACK;
        }

        if(type == UserGameCommand.CommandType.CONNECT) {
            handleConnect(session, command.getAuthToken(), username, command.getGameID(), gameData, color);
        }
        else if(type == UserGameCommand.CommandType.MAKE_MOVE) {
            MakeMoveCommand makeMoveCommand = (MakeMoveCommand) command;
            ChessMove move = makeMoveCommand.getMove();
            handleMakeMove(session, command.getAuthToken(), username, command.getGameID(), gameData, color, move);
        }
        else if(type == UserGameCommand.CommandType.RESIGN) {
            handleResign(session, command.getAuthToken(), username, command.getGameID(), gameData, color);
        }
        else if(type == UserGameCommand.CommandType.LEAVE) {
            handleLeave(session, command.getAuthToken(), username, command.getGameID(), gameData);
        }
    }

    private void handleConnect(Session session, String authToken, String username, int gameID, GameData gameData, TeamColor color) {
        connections.add(authToken, gameID, session);
        String notificationString = username + " has joined the game as ";

        if(color == TeamColor.WHITE) {
            notificationString += "White";
        }
        else if(color == TeamColor.BLACK) {
            notificationString += "Black";
        }
        else {
            notificationString += "an observer";
        }
        broadcastNotification(gameID, authToken, notificationString);

        sendLoadGame(session, gameData.game());
    }

    private void handleMakeMove(Session session, String authToken, String username, int gameID, GameData gameData, TeamColor color, ChessMove move) {
        if(color == null) {
            sendError(session, "ERROR: You can't make moves, as you aren't a player");
            return;
        }

        if(gameData.resigned() != null) {
            String colorString = (gameData.resigned() == TeamColor.BLACK) ? "Black" : "White";
            sendError(session, "ERROR: The game is over because " + colorString + " resigned.");
            return;
        }
        ChessGame game = gameData.game();
        if(game.getTeamTurn() != color) {
            sendError(session, "ERROR: It isn't your turn");
            return;
        }
        if(game.isInCheckmate(color)) {
            sendError(session, "ERROR: You cannot move because you are in checkmate");
            return;
        }
        if(game.isInStalemate(color)) {
            sendError(session, "ERROR: You cannot move because you are in stalemate");
            return;
        }

        try {
            game.makeMove(move);
        }
        catch(InvalidMoveException ex) {
            sendError(session, "ERROR: Invalid move");
            return;
        }

        gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game, null);
        try {
            gameDAO.updateGame(gameData);
        }
        catch(Exception ex) {
            sendError(session, "ERROR: The move could not be made");
            return;
        }

        broadcastLoadGame(gameID, null, game);

        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        String notificationString = username + " moved " + encode(startPosition) + " to " + encode(endPosition);
        broadcastNotification(gameID, authToken, notificationString);

        if(game.isInCheckmate(game.getTeamTurn())) {
            notificationString = game.getTeamTurn().toString() + " is in checkmate";
            broadcastNotification(gameID, null, notificationString);
        }
        else if(game.isInCheck(game.getTeamTurn())) {
            notificationString = game.getTeamTurn().toString() + " is in check";
            broadcastNotification(gameID, null, notificationString);
        }
        else if(game.isInStalemate(game.getTeamTurn())) {
            notificationString = "The game is in stalemate";
            broadcastNotification(gameID, null, notificationString);
        }
    }

    private String encode(ChessPosition position) {
        String output = "";
        output += switch(position.getColumn()) {
            case 1:
                yield "A";
            case 2:
                yield "B";
            case 3:
                yield "C";
            case 4:
                yield "D";
            case 5:
                yield "E";
            case 6:
                yield "F";
            case 7:
                yield "G";
            case 8:
                yield "H";
            default:
                yield "?";
        };

        output += ((Integer) position.getRow()).toString();

        return output;
    }

    private void handleResign(Session session, String authToken, String username, int gameID, GameData gameData, TeamColor color) {
        if(color == null) {
            sendError(session, "ERROR: You can't resign, as you aren't a player");
            return;
        }

        if(gameData.resigned() != null
                || gameData.game().isInCheckmate(color)
                || gameData.game().isInStalemate(color)) {
            sendError(session, "ERROR: The game is already over");
            return;
        }

        gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), gameData.game(), color);
        try {
            gameDAO.updateGame(gameData);
        }
        catch(Exception ex) {
            sendError(session, "ERROR: The game could not be resigned");
        }

        String notificationString = username + " resigned";
        broadcastNotification(gameID, null, notificationString);
    }

    private void handleLeave(Session session, String authToken, String username, int gameID, GameData gameData) {
        connections.remove(authToken);

        String notificationString = username + " left";
        broadcastNotification(gameID, authToken, notificationString);

        if(username.equals(gameData.whiteUsername())) {
            gameData = new GameData(gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(), gameData.game(), gameData.resigned());
        }
        else if(username.equals(gameData.blackUsername())) {
            gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(), gameData.game(), gameData.resigned());
        }
        else {
            return;
        }

        try {
            gameDAO.updateGame(gameData);
        }
        catch(Exception ex) {
            sendError(session, "ERROR: An error ocurred while leaving the game");
            return;
        }
    }

    private void sendError(Session session, String errorString) {
        ErrorMessage errorMessage = new ErrorMessage(errorString);
        try {
            session.getRemote().sendString(gson.toJson(errorMessage));
        }
        catch(Exception ignored) { }
    }

    private void sendLoadGame(Session session, ChessGame game) {
        LoadGameMessage loadGameMessage = new LoadGameMessage(game);
        try {
            session.getRemote().sendString(gson.toJson(loadGameMessage));
        }
        catch(Exception ignored) { }
    }

    private void broadcastNotification(int gameID, String excludeAuthToken, String string) {
        NotificationMessage notificationMessage = new NotificationMessage(string);
        try {
            connections.broadcast(gameID, excludeAuthToken, notificationMessage);
        }
        catch(Exception ignored) { }
    }

    private void broadcastLoadGame(int gameID, String excludeAuthToken, ChessGame game) {
        LoadGameMessage loadGameMessage = new LoadGameMessage(game);
        try {
            connections.broadcast(gameID, excludeAuthToken, loadGameMessage);
        }
        catch(Exception ignored) { }
    }
}