package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import request.JoinGameRequest;
import result.ServiceException;

public class JoinGameService extends Service {

    public void joinGame(String authToken, JoinGameRequest joinGameRequest) throws ServiceException {
        AuthData authData = checkAuthorization(authToken);
        String username = authData.username();

        GameData gameData = gameDAO.getGame(joinGameRequest.gameID());
        if(gameData == null) {
            throw new ServiceException("Error: bad request", 400);
        }
        else if(joinGameRequest.playerColor() == ChessGame.TeamColor.WHITE
                && gameData.whiteUsername() == null) {
            gameData = new GameData(gameData.gameID(), username, gameData.blackUsername(), gameData.gameName(), gameData.game());
        }
        else if(joinGameRequest.playerColor() == ChessGame.TeamColor.BLACK
                && gameData.blackUsername() == null) {
            gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), username, gameData.gameName(), gameData.game());
        }
        else {
            throw new ServiceException("Error: already taken", 403);
        }

        try {
            gameDAO.updateGame(gameData);
        }
        catch(DataAccessException ex) {
            throw new ServiceException("Error: game update failed", 500);
        }
    }

}
