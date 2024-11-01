package dataaccess.sql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO {

    static {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    static {
        String command = """
                CREATE TABLE IF NOT EXISTS auth (
                authToken CHAR(36) NOT NULL PRIMARY KEY,
                username VARCHAR(20) NOT NULL
                )
                """;
        try(Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement(command)) {
                ps.executeUpdate();
            }
        }
        catch(DataAccessException | SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        String command = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        try(Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement(command)) {
                ps.setString(1, authData.authToken());
                ps.setString(2, authData.username());

                ps.executeUpdate();
            }
        }
        catch(SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public AuthData getAuth(String authToken) {
        String command = """
                SELECT username
                FROM auth
                WHERE authToken = ?
                """;
        AuthData authData = null;

        try(Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement(command)) {
                ps.setString(1, authToken);

                try(ResultSet rs = ps.executeQuery()) {
                    if(rs.next()) {
                        String username = rs.getString(1);
                        authData = new AuthData(authToken, username);
                    }
                }
            }

        }
        catch(DataAccessException | SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }

        return authData;
    }

    @Override
    public void deleteAuth(AuthData authData) {
        String command = """
                DELETE FROM auth
                WHERE authToken = ?
                """;
        try(Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement(command)) {
                ps.setString(1, authData.authToken());

                ps.executeUpdate();
            }
        }
        catch(DataAccessException | SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }

    }

    @Override
    public void clear() {
        String command = "DELETE FROM auth";
        try(Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement(command)) {
                ps.executeUpdate();
            }
        }
        catch(DataAccessException | SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}
