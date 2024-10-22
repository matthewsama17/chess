package service;

import dataaccess.*;
import dataaccess.memory.*;

public class Service {
    AuthDAO authDAO = new MemoryAuthDAO();
    GameDAO gameDAO = new MemoryGameDAO();
    UserDAO userDAO = new MemoryUserDAO();
}
