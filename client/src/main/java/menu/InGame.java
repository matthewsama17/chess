package menu;

import serverfacade.ServerFacade;
import ui.BoardDrawer;

public class InGame {
    ServerFacade facade;
    String authToken;

    public InGame(ServerFacade facade) {
        this.facade = facade;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public Menu.MenuStage eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");

        if(tokens.length == 0) {
            printHelp();
            return Menu.MenuStage.inGame;
        }
        else if(tokens[0].equals("draw")) {
            handleDraw();
            return Menu.MenuStage.inGame;
        }
        else if(tokens[0].equals("move")) {
            if(tokens.length == 2) {
                handleSeeMove(tokens);
            }
            else if(tokens.length == 3) {
                handleMakeMove(tokens);
            }
            else {
                Menu.printError("Wrong number of arguments. Request could not be processed.");
            }
            return Menu.MenuStage.inGame;
        }
        else if(tokens[0].equals("leave")) {
            return Menu.MenuStage.postlogin;
        }
        else if(tokens[0].equals("resign")) {
            return Menu.MenuStage.postlogin;
        }
        else {
            printHelp();
            return Menu.MenuStage.inGame;
        }
    }

    public void printHelp() {
        Menu.printCommand("help");
        System.out.println(" - Display these commands");
        Menu.printCommand("draw");
        System.out.println(" - Redraws Chess Board");
        Menu.printCommand("move <position>");
        System.out.println(" - See all valid moves for the piece at the given position (Ex. 'move A1')");
        Menu.printCommand("move <position> <position>");
        System.out.println(" - Make a move in the game (Ex. 'move A1 A2')");
        Menu.printCommand("leave");
        System.out.println(" - Leave the game, allowing another player to take your place");
        Menu.printCommand("resign");
        System.out.println(" - Admit defeat to your opponent");
    }

    private void handleDraw() {
        BoardDrawer.draw();
    }

    private void handleSeeMove(String[] tokens) {
        BoardDrawer.draw();
    }

    private void handleMakeMove(String[] tokens) {
        BoardDrawer.draw();
    }
}
