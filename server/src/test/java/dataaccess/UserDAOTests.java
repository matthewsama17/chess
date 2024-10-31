package dataaccess;

import dataaccess.sql.*;
import model.*;
import org.junit.jupiter.api.*;

public class UserDAOTests {
    static public UserDAO userDAO = new SQLUserDAO();

    @AfterEach
    public void clearForTests() {
        userDAO.clear();
    }

    @Test
    public void testCreateUser() {
        String username = "user";
        String hashword = "aas;dfjasldjfpeqj";
        String email = "dingus@stupid.stupid";
        UserData userData = new UserData(username, hashword, email);

        Assertions.assertDoesNotThrow(() -> userDAO.createUser(userData));
    }

    @Test
    public void testCreateUserFail() {
        String username = "user";
        String hashword = "aas;dfjasldjfpeqj";
        String email = "dingus@stupid.stupid";
        UserData userData = new UserData(username, hashword, email);

        Assertions.assertDoesNotThrow(() -> userDAO.createUser(userData));
        Assertions.assertDoesNotThrow(() -> userDAO.createUser(userData));
    }

    @Test
    public void testGetUser() {
        String username = "user";
        String hashword = "aas;dfjasldjfpeqj";
        String email = "dingus@stupid.stupid";
        UserData userData = new UserData(username, hashword, email);
        userDAO.createUser(userData);

        UserData outputUserData = userDAO.getUser(username);

        Assertions.assertEquals(userData, outputUserData);
    }

    @Test
    public void testGetUserFail() {
        String username = "notauser";
        UserData outputUserData = userDAO.getUser(username);
        Assertions.assertNull(outputUserData);
    }

    @Test
    public void testClearUser() {
        String username = "user";
        String hashword = "aas;dfjasldjfpeqj";
        String email = "dingus@stupid.stupid";
        UserData userData = new UserData(username, hashword, email);
        userDAO.createUser(userData);

        userDAO.clear();

        UserData outputUserData = userDAO.getUser(username);
        Assertions.assertNull(outputUserData);
    }
}
