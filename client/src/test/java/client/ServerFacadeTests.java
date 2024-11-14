package client;

import chess.ChessGame;
import org.junit.jupiter.api.*;
import request.*;
import result.*;
import server.Server;
import serverfacade.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @AfterEach
    public void clearForFacadeTests() {
        facade.clear();
    }

    @Test
    public void testFacadeRegister() throws ServiceException {
        RegisterRequest registerRequest = new RegisterRequest("Matthew", "aGoodPassword", "mgh57@byu.edu");
        LoginResult loginResult = facade.register(registerRequest);

        Assertions.assertEquals(loginResult.username(), registerRequest.username());
    }

    @Test
    public void testFacadeRegisterFail() throws ServiceException {
        RegisterRequest registerRequest = new RegisterRequest("Matthew2", "anotherGoodPassword", "m.3herron@gmail.com");
        facade.register(registerRequest);

        Assertions.assertThrows(ServiceException.class, () -> facade.register(registerRequest));
    }

    @Test
    public void testFacadeClear() throws ServiceException {
        RegisterRequest registerRequest = new RegisterRequest("Matthew3", "aGoodPassword", "mherron239@gmail.com");
        facade.register(registerRequest);

        facade.clear();

        Assertions.assertDoesNotThrow(() -> facade.register(registerRequest));
    }

    @Test
    public void testFacadeLogin() throws ServiceException {
        RegisterRequest registerRequest = new RegisterRequest("Matthew", "aGoodPassword", "mgh57@byu.edu");
        facade.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest(registerRequest.username(), registerRequest.password());
        LoginResult loginResult = facade.login(loginRequest);

        Assertions.assertEquals(loginResult.username(), registerRequest.username());
    }

    @Test
    public void testFacadeLoginFail() {
        LoginRequest loginRequest = new LoginRequest("Matthew", "aBadPassword");
        Assertions.assertThrows(ServiceException.class, () -> facade.login(loginRequest));
    }

    @Test
    public void testFacadeLogout() throws ServiceException {
        RegisterRequest registerRequest = new RegisterRequest("Matthew", "aGoodPassword", "mgh57@byu.edu");
        LoginResult loginResult = facade.register(registerRequest);

        Assertions.assertDoesNotThrow(() -> facade.logout(loginResult.authToken()));

        Assertions.assertThrows(ServiceException.class, () -> facade.logout(loginResult.authToken()));
    }

    @Test
    public void testFacadeLogoutFail() {
        String authToken = "aDumbAuthToken";

        Assertions.assertThrows(ServiceException.class, () -> facade.logout(authToken));
    }

    @Test
    public void testFacadeCreateGame() throws ServiceException {
        String authToken = initializeUserFacade();
        CreateGameRequest createGameRequest = new CreateGameRequest("Matthew's Game");

        Assertions.assertDoesNotThrow(() -> facade.createGame(authToken, createGameRequest));
    }

    @Test
    public void testFacadeCreateGameFail() {
        String authToken = "aDumbAuthToken";
        CreateGameRequest createGameRequest = new CreateGameRequest("Matthew's Game");

        Assertions.assertThrows(ServiceException.class, () -> facade.createGame(authToken, createGameRequest));
    }

    @Test
    public void testFacadeListGames() throws ServiceException {
        String authToken = initializeUserFacade();
        CreateGameRequest createGameRequest = new CreateGameRequest("Matthew's Game");
        facade.createGame(authToken, createGameRequest);

        Assertions.assertEquals(facade.listGames(authToken).games()[0].gameName(), createGameRequest.gameName());
    }

    @Test
    public void testFacadeListGamesFail() {
        String authToken = "aDumbAuthToken";

        Assertions.assertThrows(ServiceException.class, () -> facade.listGames(authToken));
    }

    @Test
    public void testFacadeJoinGame() throws ServiceException {
        String authToken = initializeUserFacade();
        CreateGameRequest createGameRequest = new CreateGameRequest("Matthew's Game");
        CreateGameResult createGameResult = facade.createGame(authToken, createGameRequest);

        JoinGameRequest joinGameRequest = new JoinGameRequest(ChessGame.TeamColor.WHITE, createGameResult.gameID());
        facade.joinGame(authToken, joinGameRequest);

        Assertions.assertEquals("Matthew", facade.listGames(authToken).games()[0].whiteUsername());
    }

    @Test
    public void testFacadeJoinGameFail() throws ServiceException {
        String authToken = initializeUserFacade();
        CreateGameRequest createGameRequest = new CreateGameRequest("Matthew's Game");
        CreateGameResult createGameResult = facade.createGame(authToken, createGameRequest);

        JoinGameRequest joinGameRequest = new JoinGameRequest(ChessGame.TeamColor.WHITE, createGameResult.gameID());
        facade.joinGame(authToken, joinGameRequest);

        Assertions.assertThrows(ServiceException.class, () -> facade.joinGame(authToken, joinGameRequest));
    }

    private String initializeUserFacade() throws ServiceException {
        RegisterRequest registerRequest = new RegisterRequest("Matthew", "aGoodPassword", "mgh57@byu.edu");
        LoginResult loginResult = facade.register(registerRequest);
        return loginResult.authToken();
    }

}
