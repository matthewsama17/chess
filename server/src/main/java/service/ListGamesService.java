package service;

import result.ListGamesResult;

public class ListGamesService extends Service {

    public ListGamesResult listGames(String authToken) throws ServiceException {
        checkAuthorization(authToken);

        return new ListGamesResult(gameDAO.listGames());
    }

}
