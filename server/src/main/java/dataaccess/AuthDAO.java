package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData authData);
    AuthData getAuth(String authToken);
    void deleteAuth(AuthData authData);
    void clear();
}
