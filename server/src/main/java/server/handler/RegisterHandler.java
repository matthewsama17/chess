package server.handler;

import request.RegisterRequest;
import result.LoginResult;
import service.RegisterService;
import service.ServiceException;
import spark.*;

public class RegisterHandler extends Handler {
    static public Object handleRequest(Request req, Response res) {
        RegisterService registerService = new RegisterService();

        RegisterRequest registerRequest;
        try {
            registerRequest = Handler.fromJson(req.body(), RegisterRequest.class);
            if(registerRequest == null || registerRequest.username() == null
                    || registerRequest.password() == null || registerRequest.email() == null) {
                throw new Exception();
            }
        }
        catch (Error | Exception ex) {
            return Handler.handleJsonFail(res);
        }

        LoginResult loginResult;
        try {
            loginResult = registerService.register(registerRequest);
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
