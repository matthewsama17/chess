package server;

import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.delete("/db", Handler::clear);
        Spark.post("/user", Handler::register);
        Spark.post("/session", Handler::login);
        Spark.delete("/session", Handler::logout);
        Spark.get("/game", Handler::listGames);
        Spark.post("/game", Handler::createGame);
        Spark.put("/game", Handler::joinGame);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
