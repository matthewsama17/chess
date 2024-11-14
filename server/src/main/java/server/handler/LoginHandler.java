package server.handler;

import request.LoginRequest;
import result.LoginResult;
import service.LoginService;
import result.ServiceException;
import spark.*;

public class LoginHandler extends Handler {
    static public Object handleRequest(Request req, Response res) {
        LoginService loginService = new LoginService();

        LoginRequest loginRequest;
        try {
            loginRequest = Handler.fromJson(req.body(), LoginRequest.class);
            if(loginRequest == null || loginRequest.username() == null || loginRequest.password() == null) {
                throw new Exception();
            }
        }
        catch (Error | Exception ex) {
            return Handler.handleJsonFail(res);
        }

        LoginResult loginResult;
        try {
            loginResult = loginService.login(loginRequest);
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
        return Handler.toJson(loginResult);
    }
}
