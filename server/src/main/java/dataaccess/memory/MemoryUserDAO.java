package dataaccess.memory;

import dataaccess.UserDAO;
import model.UserData;
import java.util.HashSet;

public class MemoryUserDAO implements UserDAO {
    static HashSet<UserData> dataset = new HashSet<>();

    @Override
    public void createUser(UserData userData) {
        dataset.add(userData);
    }

    @Override
    public UserData getUser(String username) {
        for(UserData userData : dataset) {
            if(userData.username().equals(username)) {
                return userData;
            }
        }
        return null;
    }

    @Override
    public void clear() {
        dataset = new HashSet<>();
    }
}
