package service;

import model.GameData;
import request.CreateGameRequest;
import result.CreateGameResult;
import result.ServiceException;

public class CreateGameService extends Service {

    public CreateGameResult createGame(String authToken, CreateGameRequest createGameRequest) throws ServiceException {
        checkAuthorization(authToken);

        GameData gameData = new GameData(0, null, null, createGameRequest.gameName(),new chess.ChessGame());
        int gameID = gameDAO.createGame(gameData);

        return new CreateGameResult(gameID);
    }

}
