package service;

import dataaccess.*;
import dataaccess.sql.*;
import model.AuthData;
import result.ServiceException;

import java.util.UUID;

public class Service {

    AuthDAO authDAO = new SQLAuthDAO();
    GameDAO gameDAO = new SQLGameDAO();
    UserDAO userDAO = new SQLUserDAO();

    public String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

    protected AuthData checkAuthorization(String authToken) throws ServiceException {
        AuthData authData = authDAO.getAuth(authToken);
        if(authData == null) {
            throw new ServiceException("Error: unauthorized", 401);
        }

        return authData;
    }
}
