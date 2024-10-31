package service;

import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import request.LoginRequest;
import result.LoginResult;

public class LoginService extends Service {

    public LoginResult login(LoginRequest loginRequest) throws ServiceException {
        UserData userData = userDAO.getUser(loginRequest.username());
        if(userData == null
                || !BCrypt.checkpw(loginRequest.password(), userData.passwordHash())) {
            throw new ServiceException("Error: unauthorized",  401);
        }

        String authToken = generateAuthToken();
        AuthData authData = new AuthData(authToken, loginRequest.username());
        authDAO.createAuth(authData);

        return new LoginResult(loginRequest.username(), authToken);
    }
}
