package service;

import chess.ChessGame;
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
    public void testCreateGame() {
        String authToken = initializeUser();
        CreateGameRequest createGameRequest = new CreateGameRequest("Matthew's Game");

        Assertions.assertDoesNotThrow(() -> CreateGameService.createGame(authToken, createGameRequest));
    }

    @Test
    public void testCreateGameFail() {
        String authToken = "aDumbAuthToken";
        CreateGameRequest createGameRequest = new CreateGameRequest("Matthew's Game");

        Assertions.assertThrows(ServiceException.class, () -> CreateGameService.createGame(authToken, createGameRequest));
    }

    @Test
    public void testListGames() {
        String authToken = initializeUser();
        CreateGameRequest createGameRequest = new CreateGameRequest("Matthew's Game");
        CreateGameService.createGame(createGameRequest);

        Assertions.assertEquals(ListGamesService.listGames(authToken).games()[0].gameName(), createGameRequest.gameName());
    }

    @Test
    public void testListGamesFail() {
        String authToken = "aDumbAuthToken";

        Assertions.assertThrows(ServiceException.class, ListGamesService.listGames(authToken));
    }

    @Test
    public void testJoinGame() {
        String authToken = initializeUser();
        CreateGameRequest createGameRequest = new CreateGameRequest("Matthew's Game");
        CreateGameResult createGameResult = CreateGameService.createGame(createGameRequest);

        JoinGameRequest joinGameRequest = new JoinGameRequest(ChessGame.TeamColor.WHITE, createGameResult.gameID());
        JoinGameService.joinGame(authToken, joinGameRequest);

        Assertions.assertEquals("Matthew", ListGamesService.listGames(authToken).games()[0].whiteUsername());
    }

    @Test
    public void testJoinGameFail() {
        String authToken = initializeUser();
        CreateGameRequest createGameRequest = new CreateGameRequest("Matthew's Game");
        CreateGameResult createGameResult = CreateGameService.createGame(createGameRequest);

        JoinGameRequest joinGameRequest = new JoinGameRequest(ChessGame.TeamColor.WHITE, createGameResult.gameID());
        JoinGameService.joinGame(authToken, joinGameRequest);

        Assertions.assertThrows(ServiceException.class, () -> JoinGameService.joinGame(authToken, joinGameRequest));
    }

    private String initializeUser() {
        RegisterRequest registerRequest = new RegisterRequest("Matthew", "aGoodPassword", "mgh57@byu.edu");
        LoginResult loginResult = RegisterService.register(registerRequest);
        return loginResult.authToken();
    }
}
