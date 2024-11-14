package menu;

import serverfacade.ServerFacade;
import static ui.EscapeSequences.*;

import java.util.Scanner;

public class Menu {
    ServerFacade facade;

    public Menu(String url) {
        facade = new ServerFacade(url);
    }

    public Menu(int port) {
        facade = new ServerFacade(port);
    }

    public void run() {
        System.out.print(SET_TEXT_COLOR_YELLOW);
        System.out.println("Welcome to chess!");
        System.out.println("Please log in to begin.");

        Scanner scanner = new Scanner(System.in);
        String result = "";

        while(!result.equals("quit")) {
            printPrompt();
            result = scanner.nextLine();

            System.out.print(SET_TEXT_COLOR_YELLOW);
            System.out.println(result);
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n");
        System.out.print(RESET_TEXT_COLOR);
        System.out.print(">>> ");
        System.out.print(SET_TEXT_COLOR_GREEN);
    }
}
