package service;

import dataaccess.*;
import dataaccess.memory.*;
import model.AuthData;

import java.util.UUID;

public class Service {
    static public int lastGameID = 0;

    AuthDAO authDAO = new MemoryAuthDAO();
    GameDAO gameDAO = new MemoryGameDAO();
    UserDAO userDAO = new MemoryUserDAO();

    protected int generateGameID() {
        lastGameID += 1;
        return lastGameID;
    }

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
