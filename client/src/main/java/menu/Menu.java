package menu;

import serverfacade.ServerFacade;
import static ui.EscapeSequences.*;

import java.util.Scanner;

public class Menu {
    ServerFacade facade;
    Prelogin prelogin;
    Postlogin postlogin;
    MenuStage stage = MenuStage.prelogin;

    public Menu(String url) {
        facade = new ServerFacade(url);
        prelogin = new Prelogin(facade);
        postlogin = new Postlogin(facade);
    }

    public Menu(int port) {
        facade = new ServerFacade(port);
    }

    public enum MenuStage {
        prelogin,
        postlogin
    }

    public void run() {
        System.out.print(SET_TEXT_COLOR_YELLOW);
        System.out.println("Welcome to chess!");
        prelogin.printHelp();

        Scanner scanner = new Scanner(System.in);
        String result = "";

        System.out.print(SET_TEXT_COLOR_YELLOW);

        while(!result.equals("quit")) {
            printPrompt();
            result = scanner.nextLine();

            if(stage == MenuStage.prelogin) {
                stage = prelogin.eval(result);
            }
            else if(stage == MenuStage.postlogin) {
                stage = postlogin.eval(result, prelogin.getAuthToken());
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n");
        System.out.print(RESET_TEXT_COLOR);
        if(stage == MenuStage.postlogin) {
            System.out.print(prelogin.getUsername());
        }
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
