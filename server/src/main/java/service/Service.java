package service;

import dataaccess.*;
import dataaccess.memory.*;
import java.util.UUID;

public class Service {
    AuthDAO authDAO = new MemoryAuthDAO();
    GameDAO gameDAO = new MemoryGameDAO();
    UserDAO userDAO = new MemoryUserDAO();

    public String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}
