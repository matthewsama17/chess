package menu;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import result.ServiceException;
import serverfacade.ServerFacade;
import websocket.ServerMessageObserver;
import websocket.WebSocketFacade;
import websocket.commands.*;
import websocket.messages.*;

import static ui.EscapeSequences.RESET_BG_COLOR;

public class InGame implements ServerMessageObserver {
    ServerFacade facade;
    Menu menu;
    WebSocketFacade ws;
    String authToken;
    int gameID;
    int resigning = 0;

    public InGame(ServerFacade facade, Menu menu) {
        this.facade = facade;
        this.menu = menu;
    }

    public void addWS(WebSocketFacade ws) {
        this.ws = ws;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public void connect() {
        try {
            ws.connect(authToken, gameID);
        }
        catch(ServiceException ex) {
            Menu.printError();
        }
    }

    public Menu.MenuStage eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");

        if(resigning > 0) {
            resigning -= 1;
        }

        if(tokens.length == 0) {
            printHelp();
            return Menu.MenuStage.inGame;
        }
        else if(tokens[0].equals("draw")) {
            menu.drawBoard();
            return Menu.MenuStage.inGame;
        }
        else if(tokens[0].equals("move")) {
            if(tokens.length == 2) {
                handleSeeMove(tokens);
            }
            else if(tokens.length == 3 || tokens.length == 4) {
                handleMakeMove(tokens);
            }
            else {
                Menu.printError("Wrong number of arguments. Request could not be processed.");
            }
            return Menu.MenuStage.inGame;
        }
        else if(tokens[0].equals("leave")) {
            try {
                ws.leave(authToken, gameID);
            }
            catch(ServiceException ex) {
                Menu.printError();
            }

            return Menu.MenuStage.postlogin;
        }
        else if(tokens[0].equals("resign")) {
            if(resigning <= 0) {
                resigning = 2;
                Menu.printCommand("");
                System.out.println("Are you sure you want to resign? Doing so will end the game.");
                System.out.println("If you are, enter 'resign' a second time.");
            }
            else {
                try {
                    ws.resign(authToken, gameID);
                } catch (ServiceException ex) {
                    Menu.printError();
                }
            }

            return Menu.MenuStage.inGame;
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
        Menu.printCommand("move <position> <position> <promotion>");
        System.out.println(" - Make a move in the game that causes a promotion (Ex. 'move A2 A1 QUEEN')");
        Menu.printCommand("leave");
        System.out.println(" - Leave the game, allowing another player to take your place");
        Menu.printCommand("resign");
        System.out.println(" - Admit defeat to your opponent");
    }

    private void handleSeeMove(String[] tokens) {
        ChessPosition startPosition = decodePosition(tokens[1]);
        if(startPosition == null) {
            Menu.printError("Not a valid position");
            return;
        }

        menu.drawMoves(startPosition);
    }

    private void handleMakeMove(String[] tokens) {
        ChessPosition startPosition = decodePosition(tokens[1]);
        ChessPosition endPosition = decodePosition(tokens[2]);
        if(startPosition == null || endPosition == null) {
            Menu.printError("Not a valid position");
            return;
        }

        ChessPiece.PieceType promotionPiece = null;
        if(tokens.length == 4) {
            if(tokens[3].equals("queen")) {
                promotionPiece = ChessPiece.PieceType.QUEEN;
            }
            else if(tokens[3].equals("knight")) {
                promotionPiece = ChessPiece.PieceType.KNIGHT;
            }
            else if(tokens[3].equals("rook")) {
                promotionPiece = ChessPiece.PieceType.ROOK;
            }
            else if(tokens[3].equals("bishop")) {
                promotionPiece = ChessPiece.PieceType.BISHOP;
            }
            else {
                Menu.printError("Not a valid move");
                return;
            }
        }

        ChessMove move = new ChessMove(startPosition, endPosition, promotionPiece);
        try {
            ws.makeMove(authToken, gameID, move);
        }
        catch(ServiceException ex) {
            Menu.printError();
        }
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



    @Override
    public void notify(ServerMessage serverMessage) {
        if(serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            NotificationMessage notificationMessage = (NotificationMessage) serverMessage;
            menu.printNotification(notificationMessage.getMessage());
        }
        else if(serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
            ErrorMessage errorMessage = (ErrorMessage) serverMessage;
            menu.printServerError(errorMessage.getErrorMessage());
        }
        else if(serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            LoadGameMessage loadGameMessage = (LoadGameMessage) serverMessage;
            menu.loadGame(loadGameMessage.getGame());
        }
    }
}
