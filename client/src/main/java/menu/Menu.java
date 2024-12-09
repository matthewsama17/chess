package menu;

import chess.ChessGame;
import chess.ChessPosition;
import result.LoginResult;
import serverfacade.ServerFacade;
import ui.BoardDrawer;
import websocket.WebSocketFacade;

import static ui.EscapeSequences.*;

import java.util.Scanner;

public class Menu {
    ServerFacade facade;
    WebSocketFacade ws;static
    Prelogin prelogin;
    Postlogin postlogin;
    InGame inGame;
    MenuStage stage = MenuStage.prelogin;
    String username = null;
    ChessGame chessGame = null;
    ChessGame.TeamColor color = null;

    public Menu(String url) {
        facade = new ServerFacade(url);
        prelogin = new Prelogin(facade);
        postlogin = new Postlogin(facade);

        try {
            inGame = new InGame(facade, this);
            ws = new WebSocketFacade(url, inGame);
            inGame.addWS(ws);
        }
        catch (Exception ex) {
            printError();
        }
    }

    public Menu(int port) {
        facade = new ServerFacade(port);
    }

    public enum MenuStage {
        prelogin,
        postlogin,
        inGame
    }

    public void run() {
        System.out.print(SET_TEXT_COLOR_YELLOW);
        System.out.println("Welcome to chess!");
        prelogin.printHelp();

        Scanner scanner = new Scanner(System.in);
        String result = "";

        while(!result.equals("quit")) {
            printPrompt();
            result = scanner.nextLine();

            System.out.print(SET_TEXT_COLOR_YELLOW);

            if(stage == MenuStage.prelogin) {
                LoginResult loginResult = prelogin.eval(result);

                if(loginResult != null) {
                    postlogin.setAuthToken(loginResult.authToken());
                    inGame.setAuthToken(loginResult.authToken());
                    username = loginResult.username();
                    stage = MenuStage.postlogin;
                }
            }
            else if(stage == MenuStage.postlogin) {
                stage = postlogin.eval(result);

                if(stage == MenuStage.inGame) {
                    color = postlogin.getGameInfo().playerColor();
                    inGame.setGameID(postlogin.getGameInfo().gameID());
                    inGame.connect();
                }
            }
            else if(stage == MenuStage.inGame) {
                stage = inGame.eval(result);
                if(result.equals("quit")) {
                    result = "don't";
                }
            }
        }
        System.out.println();
    }

    public void printNotification(String message) {
        System.out.print(RESET_BG_COLOR);
        System.out.print(SET_TEXT_COLOR_YELLOW);
        System.out.println();
        System.out.println(message);
        printPrompt();
    }

    public void printServerError(String message) {
        System.out.print(RESET_BG_COLOR);
        System.out.println();
        Menu.printError(message);
        printPrompt();
    }

    public void loadGame(ChessGame chessGame) {
        this.chessGame = chessGame;

        System.out.print(RESET_BG_COLOR);
        System.out.println();
        this.drawBoard();
    }

    public void drawBoard() {
        if(chessGame == null) {
            printError("No board could be located.");
        }

        BoardDrawer.draw(chessGame.getBoard(), color);
    }

    public void drawMoves(ChessPosition startPosition) {
        if(chessGame == null) {
            printError("No board could be located.");
        }

        BoardDrawer.drawMoves(chessGame.getBoard(), color, chessGame.validMoves(startPosition), startPosition);
    }

    private void printPrompt() {
        System.out.print(RESET_BG_COLOR);
        System.out.print("\n");
        System.out.print(SET_TEXT_COLOR_BLUE);
        if(stage != MenuStage.prelogin) {
            System.out.print(username);
        }
        System.out.print(RESET_TEXT_COLOR);
        System.out.print(">>> ");
        System.out.print(SET_TEXT_COLOR_GREEN);
    }

    public static void printCommand(String command) {
        System.out.print(SET_TEXT_COLOR_GREEN);
        System.out.print(command);
        System.out.print(SET_TEXT_COLOR_YELLOW);
    }

    public static void printError(String error) {
        System.out.print(SET_TEXT_COLOR_RED);
        System.out.println(error);
        System.out.print(SET_TEXT_COLOR_YELLOW);
    }

    public static void printError() {
        printError("An unknown error occurred.");
    }
}
