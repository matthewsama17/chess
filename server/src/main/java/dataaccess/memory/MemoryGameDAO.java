package dataaccess.memory;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

import java.util.ArrayList;
import java.util.List;

public class MemoryGameDAO implements GameDAO {

    static public int lastGameID = 0;
    static List<GameData> dataset = new ArrayList<>();

    @Override
    public int createGame(GameData gameData) {
        int gameID = generateGameID();

        String whiteUsername = gameData.whiteUsername();
        String blackUsername = gameData.blackUsername();
        String gameName = gameData.gameName();
        ChessGame game = gameData.game();
        ChessGame.TeamColor resigned  = gameData.resigned();
        GameData newGameData = new GameData(gameID, whiteUsername, blackUsername, gameName, game, resigned);

        dataset.add(newGameData);
        return gameID;
    }

    private int generateGameID() {
        lastGameID += 1;
        return lastGameID;
    }

    @Override
    public GameData getGame(int gameID) {
        for(GameData gameData : dataset) {
            if(gameData.gameID() == gameID) {
                return gameData;
            }
        }
        return null;
    }

    @Override
    public GameData[] listGames() {
        GameData[] games = new GameData[dataset.size()];
        return dataset.toArray(games);
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        GameData oldGameData = getGame(gameData.gameID());
        if(oldGameData == null) {
            throw new DataAccessException("updateGame tried to update a game that does not exist");
        }
        int index = dataset.indexOf(oldGameData);
        dataset.set(index, gameData);
    }

    @Override
    public void clear() {
        dataset = new ArrayList<>();
    }
}
