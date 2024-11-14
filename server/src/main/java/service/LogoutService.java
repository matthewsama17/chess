package service;

import model.AuthData;
import result.ServiceException;

public class LogoutService extends Service {

    public void logout(String authToken) throws ServiceException {
        AuthData authData = checkAuthorization(authToken);

        authDAO.deleteAuth(authData);
    }

}
