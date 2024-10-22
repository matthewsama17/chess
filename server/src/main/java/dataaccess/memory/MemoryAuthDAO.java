package dataaccess.memory;

import dataaccess.AuthDAO;
import model.AuthData;
import java.util.HashSet;

public class MemoryAuthDAO implements AuthDAO {
    static HashSet<AuthData> dataset = new HashSet<>();

    @Override
    public void createAuth(AuthData authData) {
        dataset.add(authData);
    }

    @Override
    public AuthData getAuth(String authToken) {
        for(AuthData authData : dataset) {
            if(authData.authToken().equals(authToken)) {
                return authData;
            }
        }
        return null;
    }

    @Override
    public void deleteAuth(AuthData authData) {
        dataset.remove(authData);
    }

    @Override
    public void clear() {
        dataset = new HashSet<>();
    }
}
