package menu;

import serverfacade.ServerFacade;
import static ui.EscapeSequences.*;

import java.util.Scanner;

public class Menu {
    ServerFacade facade;
    MenuStage stage = MenuStage.prelogin;

    public Menu(String url) {
        facade = new ServerFacade(url);
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
        Prelogin.printHelp();

        Scanner scanner = new Scanner(System.in);
        String result = "";

        System.out.print(SET_TEXT_COLOR_YELLOW);

        while(!result.equals("quit")) {
            printPrompt();
            result = scanner.nextLine();

            if(stage == MenuStage.prelogin) {
                stage = Prelogin.eval(result);
            }
            else if(stage == MenuStage.postlogin) {
                stage = Postlogin.eval(result);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n");
        System.out.print(RESET_TEXT_COLOR);
        System.out.print(">>> ");
        System.out.print(SET_TEXT_COLOR_GREEN);
    }

    public static void printCommand(String command) {
        System.out.print(SET_TEXT_COLOR_GREEN);
        System.out.print(command);
        System.out.print(SET_TEXT_COLOR_YELLOW);
    }
}
