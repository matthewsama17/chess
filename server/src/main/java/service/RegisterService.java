package service;

import model.AuthData;
import model.UserData;
import request.RegisterRequest;
import result.LoginResult;

public class RegisterService extends Service {

    public LoginResult register(RegisterRequest registerRequest) throws ServiceException {
        if(userDAO.getUser(registerRequest.username()) != null) {
            throw new ServiceException("Error: already taken", 403);
        }

        UserData userData = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        userDAO.createUser(userData);

        String authToken = generateAuthToken();
        AuthData authData = new AuthData(authToken, registerRequest.username());
        authDAO.createAuth(authData);

        return new LoginResult(registerRequest.username(), authToken);
    }
}