package dataaccess;

import model.GameData;

public interface GameDAO {
    void createGame(GameData gameData);
    int generateGameID();
    GameData getGame(int gameID);
    GameData[] listGames();
    void updateGame(GameData gameData) throws DataAccessException;
    void clear();
}
