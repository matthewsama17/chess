package serverfacade;

import request.CreateGameRequest;
import request.JoinGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.*;
import service.ServiceException;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public ServerFacade(int port) {
        serverUrl = "http://localhost:" + port;
    }

    public LoginResult register(RegisterRequest registerRequest) throws ServiceException {
        return null;
    }

    public LoginResult login(LoginRequest loginRequest) throws ServiceException {
        return null;
    }

    public void logout(String authToken) throws ServiceException {

    }

    public CreateGameResult createGame(String authToken, CreateGameRequest createGameRequest) throws ServiceException {
        return null;
    }

    public ListGamesResult listGames(String authToken) throws ServiceException {
        return null;
    }

    public void joinGame(String authToken, JoinGameRequest joinGameRequest) throws ServiceException {

    }

    public void clear() {

    }

}
