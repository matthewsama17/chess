package dataaccess;

import model.GameData;
import java.util.List;

public interface GameDAO {
    void createGame(GameData gameData);
    GameData getGame(int gameID);
    List<GameData> listGames();
    void updateGame(GameData gameData) throws DataAccessException;
    void clear();
}
