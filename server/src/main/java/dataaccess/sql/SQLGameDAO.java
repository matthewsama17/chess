package dataaccess.sql;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import model.GameData;

import com.google.gson.Gson;

import java.sql.*;

public class SQLGameDAO implements GameDAO {

    private Gson gson = new Gson();

    static {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    static {
        String SQLCommand = """
                CREATE TABLE IF NOT EXISTS game (
                gameID INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
                whiteUsername VARCHAR(20),
                blackUsername VARCHAR(20),
                gameName VARCHAR(20) NOT NULL,
                gameJson VARCHAR(2048) NOT NULL
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
    public int createGame(GameData gameData) {
        String SQLCommand = """
                INSERT INTO game (whiteUsername, blackUsername, gameName, gameJson) VALUES (?, ?, ?, ?)
                """;
        int gameID = 0;

        try(Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement(SQLCommand, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, gameData.whiteUsername());
                ps.setString(2, gameData.blackUsername());
                ps.setString(3, gameData.gameName());
                String gameJson = gson.toJson(gameData.game());
                ps.setString(4, gameJson);

                if(ps.executeUpdate() == 1) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        rs.next();
                        gameID =  rs.getInt(1);
                    }
                }
            }
        }
        catch(DataAccessException | SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        return gameID;
    }

    @Override
    public GameData getGame(int gameID) {
        String SQLCommand = """
                SELECT whiteUsername, blackUsername, gameName, gameJson
                FROM game
                WHERE gameID = ?
                """;
        GameData gameData = null;

        try(Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement(SQLCommand)) {
                ps.setInt(1, gameID);

                try(ResultSet rs = ps.executeQuery()) {
                    if(rs.next()) {
                        String whiteUsername = rs.getString(1);
                        String blackUsername = rs.getString(2);
                        String gameName = rs.getString(3);
                        String gameJson = rs.getString(4);

                        ChessGame game = gson.fromJson(gameJson, ChessGame.class);

                        gameData = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
                    }
                }
            }
        }
        catch(DataAccessException | SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }

        return gameData;
    }

    @Override
    public GameData[] listGames() {
        return new GameData[0];
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {

    }

    @Override
    public void clear() {
        String SQLCommand = "DELETE FROM game";
        String SQLCommand2 = "ALTER TABLE game AUTO_INCREMENT = 1";

        try(Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement(SQLCommand)) {
                ps.executeUpdate();
            }
            try(PreparedStatement ps = conn.prepareStatement(SQLCommand2)) {
                ps.executeUpdate();
            }
        }
        catch(DataAccessException | SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}
