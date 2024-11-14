package service;

import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import request.RegisterRequest;
import result.LoginResult;
import result.ServiceException;

public class RegisterService extends Service {

    public LoginResult register(RegisterRequest registerRequest) throws ServiceException {
        String hashedPassword = BCrypt.hashpw(registerRequest.password(), BCrypt.gensalt());

        UserData userData = new UserData(registerRequest.username(), hashedPassword, registerRequest.email());

        try {
            userDAO.createUser(userData);
        }
        catch (DataAccessException ex) {
            throw new ServiceException("Error: already taken", 403);
        }


        String authToken = generateAuthToken();
        AuthData authData = new AuthData(authToken, registerRequest.username());
        try {
            authDAO.createAuth(authData);
        } catch (DataAccessException ex) {
            throw new RuntimeException(ex.getMessage());
        }

        return new LoginResult(registerRequest.username(), authToken);
    }
}