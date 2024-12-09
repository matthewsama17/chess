package websocket;

import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import result.ServiceException;
import websocket.commands.*;
import websocket.messages.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    ServerMessageObserver serverMessageObserver;
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(ServerMessage.class,
                    (JsonDeserializer<ServerMessage>) (el, type, ctx) -> {
                ServerMessage serverMessage = null;
                if(el.isJsonObject()) {
                    String serverMessageType = el.getAsJsonObject().get("serverMessageType").getAsString();
                    switch(ServerMessage.ServerMessageType.valueOf(serverMessageType)) {
                        case NOTIFICATION -> serverMessage = ctx.deserialize(el, NotificationMessage.class);
                        case ERROR -> serverMessage = ctx.deserialize(el, ErrorMessage.class);
                        case LOAD_GAME -> serverMessage = ctx.deserialize(el, LoadGameMessage.class);
                    }
                }
                return serverMessage;
                    })
            .create();

    public WebSocketFacade(String url, ServerMessageObserver serverMessageObserver) throws ServiceException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.serverMessageObserver = serverMessageObserver;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
                    serverMessageObserver.notify(serverMessage);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ServiceException(ex.getMessage(), 500);
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) { }

    public void connect(String authToken, int gameID) throws ServiceException {
        UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        }
        catch(Exception ex) {
            throw new ServiceException(ex.getMessage(), 500);
        }
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws ServiceException {
        MakeMoveCommand makeMoveCommand = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move);
        try {
            this.session.getBasicRemote().sendText(gson.toJson(makeMoveCommand));
        }
        catch(Exception ex) {
            throw new ServiceException(ex.getMessage(), 500);
        }
    }

    public void leave(String authToken, int gameID) throws ServiceException {
        UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        try {
            this.session.getBasicRemote().sendText(gson.toJson(userGameCommand));
        }
        catch(Exception ex) {
            throw new ServiceException(ex.getMessage(), 500);
        }
    }

    public void resign(String authToken, int gameID) throws ServiceException {
        UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        try {
            this.session.getBasicRemote().sendText(gson.toJson(userGameCommand));
        }
        catch(Exception ex) {
            throw new ServiceException(ex.getMessage(), 500);
        }
    }
}
