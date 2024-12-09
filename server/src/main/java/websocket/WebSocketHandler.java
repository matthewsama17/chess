package websocket;

import com.google.gson.Gson;
import dataaccess.*;
import dataaccess.sql.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        connections.add(command.getAuthToken(), session);

        ServerMessage serverMessage = new NotificationMessage("This is a notification!");
        connections.broadcast(command.getAuthToken(), serverMessage);
    }
}