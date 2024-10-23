package service;

import chess.ChessGame;
import model.GameData;
import request.CreateGameRequest;
import result.CreateGameResult;

public class CreateGameService extends Service {

    public CreateGameResult createGame(String authToken, CreateGameRequest createGameRequest) throws ServiceException {
        checkAuthorization(authToken);

        int gameID = generateGameID();
        GameData gameData = new GameData(gameID, null, null, createGameRequest.gameName(),new chess.ChessGame());
        gameDAO.createGame(gameData);

        return new CreateGameResult(gameID);
    }

}
