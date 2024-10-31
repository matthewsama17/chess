package dataaccess;

import dataaccess.sql.*;
import model.*;
import org.junit.jupiter.api.*;

public class AuthDAOTests {
    static public AuthDAO authDAO = new SQLAuthDAO();

    @AfterEach
    public void clearForTests() {
        authDAO.clear();
    }

    @Test
    public void testCreateAuth() {
        String username = "aName";
        String authToken = "anAuthToken";
        AuthData authData = new AuthData(authToken, username);

        Assertions.assertDoesNotThrow(() -> authDAO.createAuth(authData));
    }

    @Test
    public void testCreateAuthFail() {
        String username = "aName";
        String authToken = "anAuthToken";
        AuthData authData = new AuthData(authToken, username);

        authDAO.createAuth(authData);
        Assertions.assertDoesNotThrow(() -> authDAO.createAuth(authData));
    }

    @Test
    public void testGetAuth() {
        String username = "username";
        String authToken = "GoodAuthToken";
        AuthData authData = new AuthData(authToken, username);
        authDAO.createAuth(authData);

        AuthData outputAuthData = authDAO.getAuth(authToken);

        Assertions.assertEquals(authData, outputAuthData);
    }

    @Test
    public void testGetAuthFail() {
        String authToken = "RealBadAuthToken";
        AuthData authData = authDAO.getAuth(authToken);

        Assertions.assertNull(authData);
    }

    @Test
    public void testClearAuth() {
        String username = "Matthew";
        String authToken = "MediocreAuthToken";
        AuthData authData = new AuthData(authToken, username);
        authDAO.createAuth(authData);

        authDAO.clear();

        AuthData outputAuthData = authDAO.getAuth(authToken);

        Assertions.assertNull(outputAuthData);
    }

    @Test
    public void testDeleteAuth() {
        String username = "Matthew";
        String authToken = "MediocreAuthToken";
        AuthData authData = new AuthData(authToken, username);
        authDAO.createAuth(authData);

        authDAO.deleteAuth(authData);

        AuthData outputAuthData = authDAO.getAuth(authToken);

        Assertions.assertNull(outputAuthData);
    }

    @Test
    public void testDeleteAuthFail() {
        String username = "Matthew";
        String authToken = "MediocreAuthToken";
        AuthData authData = new AuthData(authToken, username);
        authDAO.createAuth(authData);

        String othername = "Matt";
        String otherToken = "MediumAuthToken";
        AuthData otherData = new AuthData(otherToken, othername);
        authDAO.createAuth(otherData);

        authDAO.deleteAuth(otherData);

        AuthData outputAuthData = authDAO.getAuth(authToken);

        Assertions.assertEquals(authData, outputAuthData);
    }
}
