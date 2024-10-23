package service;

public class ClearService extends Service {

    public void clear() {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }

}
