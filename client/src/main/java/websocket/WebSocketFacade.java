package websocket;

import com.google.gson.Gson;
import result.ServiceException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    ServerMessageObserver serverMessageObserver;

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
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    serverMessageObserver.notify(serverMessage);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ServiceException(ex.getMessage(), 500);
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) { }

    public void sendWS() throws ServiceException {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, "BadAuthToken", 700);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
            this.session.close();
        }
        catch (IOException ex) {
            throw new ServiceException(ex.getMessage(), 500);
        }
    }
}
