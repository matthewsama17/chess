package service;

import model.AuthData;
import model.UserData;
import request.LoginRequest;
import result.LoginResult;

public class LoginService extends Service {

    public LoginResult login(LoginRequest loginRequest) throws ServiceException {
        UserData userData = userDAO.getUser(loginRequest.username());
        if(userData == null
                || !userData.password().equals(loginRequest.password())) {
            throw new ServiceException("Error: unauthorized",  401);
        }

        String authToken = generateAuthToken();
        AuthData authData = new AuthData(authToken, loginRequest.username());
        authDAO.createAuth(authData);

        return new LoginResult(loginRequest.username(), authToken);
    }
}
