package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDAO;
import model.UserData;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO {

    static {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    static {
        String SQLCommand = """
                CREATE TABLE IF NOT EXISTS user (
                username VARCHAR(20) NOT NULL PRIMARY KEY,
                passwordHash CHAR(60) NOT NULL,
                email VARCHAR(30)
                )
                """;
        try(Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement(SQLCommand)) {
                ps.executeUpdate();
            }
        }
        catch(DataAccessException | SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        String SQLCommand = "INSERT INTO user (username, passwordHash, email) VALUES (?, ?, ?)";
        try(Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement(SQLCommand)) {
                ps.setString(1, userData.username());
                ps.setString(2, userData.passwordHash());
                ps.setString(3, userData.email());

                ps.executeUpdate();
            }
        }
        catch(SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }

    }

    @Override
    public UserData getUser(String username) {
        String SQLCommand = """
                SELECT passwordHash, email
                FROM user
                WHERE username = ?
                """;
        UserData userData = null;

        try(Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement(SQLCommand)) {
                ps.setString(1, username);

                try(ResultSet rs = ps.executeQuery()) {
                    if(rs.next()) {
                        String passwordHash = rs.getString(1);
                        String email = rs.getString(2);
                        userData = new UserData(username, passwordHash, email);
                    }
                }
            }
        }
        catch(DataAccessException | SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }

        return userData;
    }

    @Override
    public void clear() {
        String SQLCommand = """
                DELETE FROM user
                """;
        try(Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement(SQLCommand)) {
                ps.executeUpdate();
            }
        }
        catch(DataAccessException | SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}
