package menu;

import chess.ChessPosition;
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
        ChessPosition startPosition = decodePosition(tokens[1]);
        if(startPosition == null) {
            Menu.printError("Not a valid position");
            return;
        }

        System.out.println(startPosition);
    }

    private void handleMakeMove(String[] tokens) {
        ChessPosition startPosition = decodePosition(tokens[1]);
        ChessPosition endPosition = decodePosition(tokens[2]);
        if(startPosition == null || endPosition == null) {
            Menu.printError("Not a valid position");
            return;
        }

        System.out.println(startPosition);
        System.out.println(endPosition);
    }

    private ChessPosition decodePosition(String input) {
        input = input.toUpperCase();

        if(input.length() != 2) {
            return null;
        }

        int col = switch(input.charAt(0)) {
            case 'A':
                yield 1;
            case 'B':
                yield 2;
            case 'C':
                yield 3;
            case 'D':
                yield 4;
            case 'E':
                yield 5;
            case 'F':
                yield 6;
            case 'G':
                yield 7;
            case 'H':
                yield 8;
            default:
                yield 0;
        };

        int row = switch(input.charAt(1)) {
            case '1':
                yield 1;
            case '2':
                yield 2;
            case '3':
                yield 3;
            case '4':
                yield 4;
            case '5':
                yield 5;
            case '6':
                yield 6;
            case '7':
                yield 7;
            case '8':
                yield 8;
            default:
                yield 0;
        };

        if(row == 0 || col == 0) {
            return null;
        }

        return new ChessPosition(row, col);
    }
}
