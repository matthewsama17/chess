package service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import request.*;
import result.*;

public class ServiceTests {

    @AfterEach
    public void clearForTests() {
        ClearService.clear();
    }

    @Test
    public void testRegister() {
        RegisterRequest registerRequest = new RegisterRequest("Matthew", "aGoodPassword", "mgh57@byu.edu");
        LoginResult loginResult = RegisterService.register(registerRequest);

        Assertions.assertEquals(loginResult.username(), registerRequest.username());
    }

    @Test
    public void testRegisterFail() {
        RegisterRequest registerRequest = new RegisterRequest("Matthew2", "anotherGoodPassword", "m.3herron@gmail.com");
        RegisterService.register(registerRequest);

        Assertions.assertThrows(ServiceException.class, () -> RegisterService.register(registerRequest));
    }

    @Test
    public void testClear() {
        RegisterRequest registerRequest = new RegisterRequest("Matthew3", "aGoodPassword", "mherron239@gmail.com");
        RegisterService.register(registerRequest);

        ClearService.clear();

        Assertions.assertDoesNotThrow(() -> RegisterService.register(registerRequest));
    }

    @Test
    public void testLogin() {
        RegisterRequest registerRequest = new RegisterRequest("Matthew", "aGoodPassword", "mgh57@byu.edu");
        RegisterService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest(registerRequest.username(), registerRequest.password());
        LoginResult loginResult = LoginService.login(loginRequest);

        Assertions.assertEquals(loginResult.username(), registerRequest.username());
    }

    @Test
    public void testLoginFail() {
        LoginRequest loginRequest = new LoginRequest("Matthew", "aBadPassword");
        Assertions.assertThrows(ServiceException.class, () -> LoginService.login(loginRequest));
    }

    @Test
    public void testLogout() {
        RegisterRequest registerRequest = new RegisterRequest("Matthew", "aGoodPassword", "mgh57@byu.edu");
        LoginResult loginResult = RegisterService.register(registerRequest);

        LogoutService.logout(loginResult.authToken());
        Assertions.assertThrows(ServiceException.class, () -> LoginService.login(loginRequest));
    }

    @Test
    public void testLogoutFail() {
        String authToken = "aDumbAuthToken";

        Assertions.assertThrows(ServiceException.class, () -> LogoutService.logout(authToken));
    }

    @Test
    public void testListGames() {

    }

    @Test
    public void testListGamesFail() {

    }

    @Test
    public void testCreateGame() {

    }

    @Test
    public void testCreateGameFail() {

    }

    @Test
    public void testJoinGame() {

    }

    @Test
    public void testJoinGameFail() {

    }

    private String initializeUser() {
        RegisterRequest registerRequest = new RegisterRequest("Matthew", "aGoodPassword", "mgh57@byu.edu");
        LoginResult loginResult = RegisterService.register(registerRequest);
        return loginResult.authToken();
    }
}
