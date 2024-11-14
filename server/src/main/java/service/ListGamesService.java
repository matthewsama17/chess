package service;

import result.ListGamesResult;
import result.ServiceException;

public class ListGamesService extends Service {

    public ListGamesResult listGames(String authToken) throws ServiceException {
        checkAuthorization(authToken);

        return new ListGamesResult(gameDAO.listGames());
    }

}
