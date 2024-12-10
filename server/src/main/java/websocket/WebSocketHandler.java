package websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
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

        if(type == UserGameCommand.CommandType.CONNECT) {
            handleConnect(session, command.getAuthToken(), username, command.getGameID(), gameData);
        }
        else if(type == UserGameCommand.CommandType.MAKE_MOVE) {

        }
        else if(type == UserGameCommand.CommandType.RESIGN) {
            handleResign(session, command.getAuthToken(), username, command.getGameID(), gameData);
        }
        else if(type == UserGameCommand.CommandType.LEAVE) {
            handleLeave(session, command.getAuthToken(), username, command.getGameID(), gameData);
        }
    }

    private void handleConnect(Session session, String authToken, String username, int gameID, GameData gameData) {
        connections.add(authToken, gameID, session);
        String notificationString = username + " has joined the game as ";

        if(username.equals(gameData.whiteUsername())) {
            notificationString += "White";
        }
        else if(username.equals(gameData.blackUsername())) {
            notificationString += "Black";
        }
        else {
            notificationString += "an observer";
        }
        broadcastNotification(gameID, authToken, notificationString);

        sendLoadGame(session, gameData.game());
    }

    private void handleResign(Session session, String authToken, String username, int gameID, GameData gameData) {
        ChessGame.TeamColor color = null;

        if(username.equals(gameData.whiteUsername())) {
            color = ChessGame.TeamColor.WHITE;
        }
        else if(username.equals(gameData.blackUsername())) {
            color = ChessGame.TeamColor.BLACK;
        }
        else {
            sendError(session, "ERROR: You can't resign, as you aren't a player");
            return;
        }

        if(gameData.resigned() != null) {
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
        broadcastNotification(gameID, authToken, notificationString);
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
        }
    }

    private void sendNotification(Session session, String string) {
        NotificationMessage notificationMessage = new NotificationMessage(string);
        try {
            session.getRemote().sendString(gson.toJson(notificationMessage));
        }
        catch(Exception ignored) { }
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