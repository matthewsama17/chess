package server.handler;

import service.LogoutService;
import service.ServiceException;
import spark.*;

public class LogoutHandler extends Handler {
    static public Object handleRequest(Request req, Response res) {
        LogoutService logoutService = new LogoutService();
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

        try {
            logoutService.logout(authToken);
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
