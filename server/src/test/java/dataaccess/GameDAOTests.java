package dataaccess;

import chess.ChessGame;
import dataaccess.sql.*;
import model.*;
import org.junit.jupiter.api.*;

public class GameDAOTests {
    static public GameDAO gameDAO = new SQLGameDAO();

    @AfterEach
    public void clearForTests() {
        gameDAO.clear();
    }

    @Test
    public void testCreateGameFail() {
        int gameID = 50;
        String whiteUsername = null;
        String blackUsername = "dinugs";
        String gameName = "VeryFunGame!";
        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(gameID,whiteUsername,blackUsername,gameName,chessGame);

        Assertions.assertDoesNotThrow(() -> gameDAO.createGame(gameData));
        Assertions.assertDoesNotThrow(() -> gameDAO.createGame(gameData));
    }

    @Test
    public void testCreateGame() {
        int gameID = 50;
        String whiteUsername = null;
        String blackUsername = "dinugs";
        String gameName = "VeryFunGame!";
        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(gameID,whiteUsername,blackUsername,gameName,chessGame);

        int newGameID = gameDAO.createGame(gameData);
        Assertions.assertNotEquals(gameID, newGameID);
    }

    @Test
    public void testGetGame() {
        int gameID = 50;
        String whiteUsername = null;
        String blackUsername = "dinugs";
        String gameName = "VeryFunGame!";
        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(gameID,whiteUsername,blackUsername,gameName,chessGame);
        int newGameID = gameDAO.createGame(gameData);

        GameData newGameData = gameDAO.getGame(newGameID);
        Assertions.assertEquals(gameName, newGameData.gameName());
    }

    @Test
    public void testGetGameFail() {
        int gameID = 55;
        GameData gameData = gameDAO.getGame(gameID);

        Assertions.assertNull(gameData);
    }

    @Test
    public void testClearGame() {
        int gameID = 50;
        String whiteUsername = null;
        String blackUsername = "dinugs";
        String gameName = "VeryFunGame!";
        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
        int newGameID = gameDAO.createGame(gameData);

        gameDAO.clear();

        GameData newGameData = gameDAO.getGame(newGameID);
        Assertions.assertNull(newGameData);
    }

    @Test
    public void testListGamesFail() {
        GameData[] gameList = gameDAO.listGames();
        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> gameList[0].game());
    }

    @Test
    public void testListGames() {
        int gameID = 50;
        String whiteUsername = null;
        String blackUsername = "dinugs";
        String gameName = "VeryFunGame!";
        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
        int newGameID = gameDAO.createGame(gameData);

        GameData newGameData = new GameData(newGameID, whiteUsername, blackUsername, gameName, chessGame);

        GameData[] expectedGameList = {newGameData};
        GameData[] gameList = gameDAO.listGames();

        Assertions.assertEquals(expectedGameList[0], gameList[0]);
    }

    @Test
    public void testUpdateGame() {
        int gameID = 50;
        String whiteUsername = null;
        String blackUsername = "dinugs";
        String gameName = "VeryFunGame!";
        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
        int newGameID = gameDAO.createGame(gameData);

        String newWhiteUsername = "obtuse";
        GameData newGameData = new GameData(newGameID, newWhiteUsername, blackUsername, gameName, chessGame);
        try {
            gameDAO.updateGame(newGameData);
        }
        catch(DataAccessException ex) {
            Assertions.fail(ex);
        }

        GameData endGameData = gameDAO.getGame(newGameID);
        Assertions.assertEquals(newWhiteUsername, endGameData.whiteUsername());
    }

    @Test
    public void testUpdateGameFail() {
        int gameID = 55;
        String whiteUsername = null;
        String blackUsername = "dinugs";
        String gameName = "VeryFunGame!";
        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(gameID,whiteUsername,blackUsername,gameName,chessGame);

        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.updateGame(gameData));
    }
}
