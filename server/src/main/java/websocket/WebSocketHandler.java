package websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import dataaccess.*;
import dataaccess.sql.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

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
        connections.add(command.getAuthToken(), session);

        ServerMessage serverMessage = new NotificationMessage(gson.toJson(command));
        if(command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
            MakeMoveCommand makeMoveCommand = (MakeMoveCommand) command;
            serverMessage = new NotificationMessage("This is a Move: " + gson.toJson(makeMoveCommand));
        }
        connections.broadcast(command.getAuthToken(), serverMessage);
    }
}