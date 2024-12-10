package dataaccess.sql;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import model.GameData;

import com.google.gson.Gson;

import java.sql.*;
import java.util.ArrayList;

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
        String command = """
                CREATE TABLE IF NOT EXISTS game (
                gameID INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
                whiteUsername VARCHAR(20),
                blackUsername VARCHAR(20),
                gameName VARCHAR(20) NOT NULL,
                gameJson VARCHAR(2048) NOT NULL,
                resigned VarChar(5)
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
    public int createGame(GameData gameData) {
        String command = """
                INSERT INTO game (whiteUsername, blackUsername, gameName, gameJson, resigned) VALUES (?, ?, ?, ?, ?)
                """;
        int gameID = 0;

        try(Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement(command, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, gameData.whiteUsername());
                ps.setString(2, gameData.blackUsername());
                ps.setString(3, gameData.gameName());
                String gameJson = gson.toJson(gameData.game());
                ps.setString(4, gameJson);
                if(gameData.resigned() != null) {
                    ps.setString(5, gameData.resigned().toString());
                }
                else {
                    ps.setString(5, null);
                }

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
        String command = """
                SELECT whiteUsername, blackUsername, gameName, gameJson, resigned
                FROM game
                WHERE gameID = ?
                """;
        GameData gameData = null;

        try(Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement(command)) {
                ps.setInt(1, gameID);

                try(ResultSet rs = ps.executeQuery()) {
                    if(rs.next()) {
                        String whiteUsername = rs.getString(1);
                        String blackUsername = rs.getString(2);
                        String gameName = rs.getString(3);
                        String gameJson = rs.getString(4);
                        String resigned = rs.getString(5);

                        ChessGame game = gson.fromJson(gameJson, ChessGame.class);
                        game = new ChessGame(game); //Gson doesn't like inner classes

                        ChessGame.TeamColor color = (resigned == null) ? null : ChessGame.TeamColor.valueOf(resigned);
                        gameData = new GameData(gameID, whiteUsername, blackUsername, gameName, game, color);
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
        ArrayList<GameData> games = new ArrayList<>();

        String command = "SELECT * FROM game";
        try(Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement(command)) {
                try(ResultSet rs = ps.executeQuery()) {
                    while(rs.next()) {
                        int gameID = rs.getInt("gameID");
                        String whiteUsername = rs.getString("whiteUsername");
                        String blackUsername = rs.getString("blackUsername");
                        String gameName = rs.getString("gameName");
                        String gameJson = rs.getString("gameJson");
                        String resigned = rs.getString("resigned");

                        ChessGame game = gson.fromJson(gameJson, ChessGame.class);
                        game = new ChessGame(game); //Gson doesn't like inner classes

                        ChessGame.TeamColor color = (resigned == null) ? null : ChessGame.TeamColor.valueOf(resigned);
                        GameData gameData = new GameData(gameID, whiteUsername, blackUsername, gameName, game, color);
                        games.add(gameData);
                    }
                }
            }
        }
        catch(DataAccessException | SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }

        GameData[] gameList = new GameData[games.size()];
        return games.toArray(gameList);
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        if(getGame(gameData.gameID()) == null) {
            throw new DataAccessException("The game to be updated does not exist.");
        }

        String command = """
                UPDATE game
                SET whiteUsername = ?, blackUsername = ?, gameName = ?, gameJson = ?, resigned = ?
                WHERE gameID = ?
                """;
        try(Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement(command)) {
                ps.setString(1, gameData.whiteUsername());
                ps.setString(2, gameData.blackUsername());
                ps.setString(3, gameData.gameName());
                String gameJson = gson.toJson(gameData.game());
                ps.setString(4, gameJson);
                if(gameData.resigned() != null) {
                    ps.setString(5, gameData.resigned().toString());
                }
                else {
                    ps.setString(5, null);
                }
                ps.setInt(6, gameData.gameID());

                ps.executeUpdate();
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void clear() {
        String command = "DELETE FROM game";
        String command2 = "ALTER TABLE game AUTO_INCREMENT = 1";

        try(Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement(command)) {
                ps.executeUpdate();
            }
            try(PreparedStatement ps = conn.prepareStatement(command2)) {
                ps.executeUpdate();
            }
        }
        catch(DataAccessException | SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}
