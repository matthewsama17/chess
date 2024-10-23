package server.handler;

import service.ClearService;
import service.ServiceException;
import spark.*;

public class ClearHandler {
    static public Object handleRequest(Request req, Response res) {
        ClearService clearService = new ClearService();

        try {
            clearService.clear();
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
