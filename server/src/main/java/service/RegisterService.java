package service;

import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import request.RegisterRequest;
import result.LoginResult;

public class RegisterService extends Service {

    public LoginResult register(RegisterRequest registerRequest) throws ServiceException {
        if(userDAO.getUser(registerRequest.username()) != null) {
            throw new ServiceException("Error: already taken", 403);
        }

        String hashedPassword = BCrypt.hashpw(registerRequest.password(), BCrypt.gensalt());

        UserData userData = new UserData(registerRequest.username(), hashedPassword, registerRequest.email());
        userDAO.createUser(userData);

        String authToken = generateAuthToken();
        AuthData authData = new AuthData(authToken, registerRequest.username());
        authDAO.createAuth(authData);

        return new LoginResult(registerRequest.username(), authToken);
    }
}