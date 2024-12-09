package server;

import server.handler.*;
import spark.*;
import websocket.WebSocketHandler;

public class Server {
    WebSocketHandler webSocketHandler = new WebSocketHandler();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/ws", webSocketHandler);

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", ClearHandler::handleRequest);
        Spark.post("/user", RegisterHandler::handleRequest);
        Spark.post("/session", LoginHandler::handleRequest);
        Spark.delete("/session", LogoutHandler::handleRequest);
        Spark.get("/game", ListGamesHandler::handleRequest);
        Spark.post("/game", CreateGameHandler::handleRequest);
        Spark.put("/game", JoinGameHandler::handleRequest);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
