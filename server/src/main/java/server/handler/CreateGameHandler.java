package server.handler;

import request.CreateGameRequest;
import result.CreateGameResult;
import service.CreateGameService;
import service.ServiceException;
import spark.*;

public class CreateGameHandler {
    static public Object handleRequest(Request req, Response res) {
        CreateGameService createGameService = new CreateGameService();
        String authToken;

        CreateGameRequest createGameRequest;
        try {
            createGameRequest = Handler.fromJson(req.body(), CreateGameRequest.class);
            if(createGameRequest == null || createGameRequest.gameName() == null) {
                throw new Exception();
            }
        }
        catch (Error | Exception ex) {
            return Handler.handleJsonFail(res);
        }

        try {
            authToken = req.headers("authorization");
            if(authToken == null) {
                throw new Exception();
            }
        }
        catch(Error | Exception ex) {
            return Handler.handleHeaderFail(res);
        }

        CreateGameResult createGameResult;
        try {
            createGameResult = createGameService.createGame(authToken, createGameRequest);
        }
        catch(ServiceException ex) {
            return Handler.handleServiceException(ex, res);
        }
        catch(Exception ex) {
            return Handler.handleException(ex, res);
        }
        catch(Error ex) {
            return Handler.handleError(ex, res);
        }

        res.status(200);
        return Handler.toJson(createGameResult);
    }
}
