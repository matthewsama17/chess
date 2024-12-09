package menu;

import result.LoginResult;
import serverfacade.ServerFacade;
import websocket.ServerMessageObserver;
import websocket.WebSocketFacade;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.*;

import java.util.Scanner;

public class Menu implements ServerMessageObserver {
    ServerFacade facade;
    WebSocketFacade ws;
    Prelogin prelogin;
    Postlogin postlogin;
    InGame inGame;
    MenuStage stage = MenuStage.prelogin;
    String username = null;

    public Menu(String url) {
        facade = new ServerFacade(url);
        prelogin = new Prelogin(facade);
        postlogin = new Postlogin(facade);

        try {
            ws = new WebSocketFacade(url, this);
            inGame = new InGame(facade, ws);
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

    @Override
    public void notify(ServerMessage message) {
        System.out.print(RESET_BG_COLOR);
        System.out.println();
        Menu.printCommand(message.toString());
        printPrompt();
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
