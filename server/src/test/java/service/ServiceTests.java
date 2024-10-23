package service;

import chess.ChessGame;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import request.*;
import result.*;

public class ServiceTests {
    static public ClearService clearService = new ClearService();
    static public RegisterService registerService = new RegisterService();
    static public LoginService loginService = new LoginService();
    static public LogoutService logoutService = new LogoutService();
    static public ListGamesService listGamesService = new ListGamesService();
    static public CreateGameService createGameService = new CreateGameService();
    static public JoinGameService joinGameService = new JoinGameService();

    @AfterEach
    public void clearForTests() {
        clearService.clear();
    }

    @Test
    public void testRegister() throws ServiceException {
        RegisterRequest registerRequest = new RegisterRequest("Matthew", "aGoodPassword", "mgh57@byu.edu");
        LoginResult loginResult = registerService.register(registerRequest);

        Assertions.assertEquals(loginResult.username(), registerRequest.username());
    }

    @Test
    public void testRegisterFail() throws ServiceException {
        RegisterRequest registerRequest = new RegisterRequest("Matthew2", "anotherGoodPassword", "m.3herron@gmail.com");
        registerService.register(registerRequest);

        Assertions.assertThrows(ServiceException.class, () -> registerService.register(registerRequest));
    }

    @Test
    public void testClear() throws ServiceException {
        RegisterRequest registerRequest = new RegisterRequest("Matthew3", "aGoodPassword", "mherron239@gmail.com");
        registerService.register(registerRequest);

        clearService.clear();

        Assertions.assertDoesNotThrow(() -> registerService.register(registerRequest));
    }

    @Test
    public void testLogin() throws ServiceException {
        RegisterRequest registerRequest = new RegisterRequest("Matthew", "aGoodPassword", "mgh57@byu.edu");
        registerService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest(registerRequest.username(), registerRequest.password());
        LoginResult loginResult = loginService.login(loginRequest);

        Assertions.assertEquals(loginResult.username(), registerRequest.username());
    }

    @Test
    public void testLoginFail() {
        LoginRequest loginRequest = new LoginRequest("Matthew", "aBadPassword");
        Assertions.assertThrows(ServiceException.class, () -> loginService.login(loginRequest));
    }

    @Test
    public void testLogout() throws ServiceException {
        RegisterRequest registerRequest = new RegisterRequest("Matthew", "aGoodPassword", "mgh57@byu.edu");
        LoginResult loginResult = registerService.register(registerRequest);

        Assertions.assertDoesNotThrow(() -> logoutService.logout(loginResult.authToken()));

        Assertions.assertThrows(ServiceException.class, () -> logoutService.logout(loginResult.authToken()));
    }

    @Test
    public void testLogoutFail() {
        String authToken = "aDumbAuthToken";

        Assertions.assertThrows(ServiceException.class, () -> logoutService.logout(authToken));
    }

    @Test
    public void testCreateGame() throws ServiceException {
        String authToken = initializeUser();
        CreateGameRequest createGameRequest = new CreateGameRequest("Matthew's Game");

        Assertions.assertDoesNotThrow(() -> createGameService.createGame(authToken, createGameRequest));
    }

    @Test
    public void testCreateGameFail() {
        String authToken = "aDumbAuthToken";
        CreateGameRequest createGameRequest = new CreateGameRequest("Matthew's Game");

        Assertions.assertThrows(ServiceException.class, () -> createGameService.createGame(authToken, createGameRequest));
    }

    @Test
    public void testListGames() throws ServiceException {
        String authToken = initializeUser();
        CreateGameRequest createGameRequest = new CreateGameRequest("Matthew's Game");
        createGameService.createGame(authToken, createGameRequest);

        Assertions.assertEquals(listGamesService.listGames(authToken).games()[0].gameName(), createGameRequest.gameName());
    }

    @Test
    public void testListGamesFail() {
        String authToken = "aDumbAuthToken";

        Assertions.assertThrows(ServiceException.class, () -> listGamesService.listGames(authToken));
    }

    @Test
    public void testJoinGame() throws ServiceException {
        String authToken = initializeUser();
        CreateGameRequest createGameRequest = new CreateGameRequest("Matthew's Game");
        CreateGameResult createGameResult = createGameService.createGame(authToken, createGameRequest);

        JoinGameRequest joinGameRequest = new JoinGameRequest(ChessGame.TeamColor.WHITE, createGameResult.gameID());
        joinGameService.joinGame(authToken, joinGameRequest);

        Assertions.assertEquals("Matthew", listGamesService.listGames(authToken).games()[0].whiteUsername());
    }

    @Test
    public void testJoinGameFail() throws ServiceException {
        String authToken = initializeUser();
        CreateGameRequest createGameRequest = new CreateGameRequest("Matthew's Game");
        CreateGameResult createGameResult = createGameService.createGame(authToken, createGameRequest);

        JoinGameRequest joinGameRequest = new JoinGameRequest(ChessGame.TeamColor.WHITE, createGameResult.gameID());
        joinGameService.joinGame(authToken, joinGameRequest);

        Assertions.assertThrows(ServiceException.class, () -> joinGameService.joinGame(authToken, joinGameRequest));
    }

    private String initializeUser() throws ServiceException {
        RegisterRequest registerRequest = new RegisterRequest("Matthew", "aGoodPassword", "mgh57@byu.edu");
        LoginResult loginResult = registerService.register(registerRequest);
        return loginResult.authToken();
    }
}
