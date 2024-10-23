package server.handler;

import request.JoinGameRequest;
import service.JoinGameService;
import service.ServiceException;
import spark.*;

public class JoinGameHandler extends Handler {
    static public Object handleRequest(Request req, Response res) {
        JoinGameService joinGameService = new JoinGameService();
        String authToken;

        JoinGameRequest joinGameRequest;
        try {
            joinGameRequest = Handler.fromJson(req.body(), JoinGameRequest.class);
            if(joinGameRequest == null || joinGameRequest.playerColor() == null) {
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

        try {
            joinGameService.joinGame(authToken, joinGameRequest);
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
        return "{}";
    }
}
