package server.handler;

import result.ListGamesResult;
import service.ListGamesService;
import service.ServiceException;
import spark.*;

public class ListGamesHandler extends Handler {
    static public Object handleRequest(Request req, Response res) {
        ListGamesService listGamesService = new ListGamesService();
        String authToken;

        try {
            authToken = req.headers("authorization");
            if(authToken == null) {
                throw new Exception();
            }
        }
        catch(Error | Exception ex) {
            return Handler.handleHeaderFail(res);
        }

        ListGamesResult listGamesResult;
        try {
            listGamesResult = listGamesService.listGames(authToken);
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
        return Handler.toJson(listGamesResult);
    }
}
