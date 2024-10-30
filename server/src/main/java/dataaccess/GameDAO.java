package dataaccess;

import model.GameData;

public interface GameDAO {
    int createGame(GameData gameData);
    GameData getGame(int gameID);
    GameData[] listGames();
    void updateGame(GameData gameData) throws DataAccessException;
    void clear();
}
