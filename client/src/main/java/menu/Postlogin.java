package menu;

import chess.ChessGame;
import model.GameData;
import ui.BoardDrawer;
import request.*;
import result.*;
import serverfacade.ServerFacade;

public class Postlogin {
    ServerFacade facade;
    String authToken;
    GameData[] games;
    JoinGameRequest gameInfo;

    public Postlogin(ServerFacade facade) {
        this.facade = facade;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public JoinGameRequest getGameInfo() {
        return gameInfo;
    }

    public Menu.MenuStage eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");

        if(tokens.length == 0) {
            printHelp();
            return Menu.MenuStage.postlogin;
        }
        else if(tokens[0].equals("create")) {
            return handleCreate(tokens);
        }
        else if(tokens[0].equals("list")) {
            return handleList();
        }
        else if(tokens[0].equals("join")) {
            return handleJoin(tokens);
        }
        else if(tokens[0].equals("observe")) {
            return handleObserve(tokens);
        }
        else if(tokens[0].equals("logout")) {
            handleLogout();
            return Menu.MenuStage.prelogin;
        }
        else if(tokens[0].equals("quit")) {
            System.out.println("Quitting...");
            handleLogout();
            return Menu.MenuStage.postlogin;
        }
        else {
            printHelp();
            return Menu.MenuStage.postlogin;
        }
    }

    public void printHelp() {
        Menu.printCommand("help");
        System.out.println(" - Display these commands");
        Menu.printCommand("create <NAME>");
        System.out.println(" - Create a game with a given name");
        Menu.printCommand("list");
        System.out.println(" - See a list of games");
        Menu.printCommand("join <ID> [WHITE|BLACK]");
        System.out.println(" - Join the game with the given id as a player");
        Menu.printCommand("observe <ID>");
        System.out.println(" - Observe the game with the given id");
        Menu.printCommand("logout");
        System.out.println(" - Log out of your account");
        Menu.printCommand("quit");
        System.out.println(" - End this program");
    }

    private Menu.MenuStage handleCreate(String[] tokens) {
        if(tokens.length != 2) {
            Menu.printError("Wrong number of arguments. Request could not be processed.");
            return Menu.MenuStage.postlogin;
        }

        CreateGameRequest createGameRequest = new CreateGameRequest(tokens[1]);
        try {
            CreateGameResult createGameResult = facade.createGame(authToken, createGameRequest);
        }
        catch(ServiceException ex) {
            if(ex.getStatusCode() == 400) {
                Menu.printError("Request could not be processed");
            }
            else if(ex.getStatusCode() == 401) {
                Menu.printError("The session has timed out.");
                return Menu.MenuStage.prelogin;
            }
            else {
                Menu.printError();
            }
            return Menu.MenuStage.postlogin;
        }

        System.out.println("Game created.");
        return Menu.MenuStage.postlogin;
    }

    private Menu.MenuStage handleList() {
        try {
            ListGamesResult listGamesResult = facade.listGames(authToken);
            GameData[] games = listGamesResult.games();
            printGames(games);
            this.games = games;
            return Menu.MenuStage.postlogin;
        }
        catch(ServiceException ex) {
            if (ex.getStatusCode() == 401) {
                Menu.printError("The session has timed out.");
                return Menu.MenuStage.prelogin;
            } else {
                Menu.printError();
            }
            return Menu.MenuStage.postlogin;
        }
    }

    private void printGames(GameData[] games) {
        System.out.println("Game ID | Game Name | White Player Name | Black Player Name");

        for(int i = 0; i < games.length; i++) {
            System.out.print(i+1);
            System.out.print(" | ");
            System.out.print(games[i].gameName());
            System.out.print(" | ");
            System.out.print(games[i].whiteUsername());
            System.out.print(" | ");
            System.out.print(games[i].blackUsername());
            System.out.println();
        }
    }

    private Menu.MenuStage handleJoin(String[] tokens) {
        if(tokens.length != 3) {
            Menu.printError("Wrong number of arguments. Request could not be processed.");
            return Menu.MenuStage.postlogin;
        }

        int gameNum;
        try {
            gameNum = Integer.parseInt(tokens[1]);
        } catch (NumberFormatException ex) {
            Menu.printError("The second argument must be an integer.");
            return Menu.MenuStage.postlogin;
        }

        ChessGame.TeamColor color = null;
        if(tokens[2].equals("white")) {
            color = ChessGame.TeamColor.WHITE;
        }
        else if(tokens[2].equals("black")) {
            color = ChessGame.TeamColor.BLACK;
        }
        else {
            Menu.printError("The last argument must be either WHITE or BLACK");
            return Menu.MenuStage.postlogin;
        }

        if(gameNum < 1 || games == null || gameNum > games.length) {
            Menu.printError("The second argument is not a valid Game ID.");
            return Menu.MenuStage.postlogin;
        }

        JoinGameRequest joinGameRequest = new JoinGameRequest(color, games[gameNum-1].gameID());
        try {
            facade.joinGame(authToken, joinGameRequest);
            gameInfo = joinGameRequest;
            System.out.println("Joined Game Successfully!");
            return Menu.MenuStage.inGame;
        }
        catch(ServiceException ex) {
            if(ex.getStatusCode() == 400) {
                Menu.printError("Request could not be processed");
            }
            else if(ex.getStatusCode() == 401) {
                Menu.printError("The session has timed out.");
                return Menu.MenuStage.prelogin;
            }
            else if(ex.getStatusCode() == 403) {
                Menu.printError("The requested position is already taken.");
            }
            else {
                Menu.printError();
            }
            return Menu.MenuStage.postlogin;

        }

    }

    private Menu.MenuStage handleObserve(String[] tokens) {
        if(tokens.length != 2) {
            Menu.printError("Wrong number of arguments. Request could not be processed.");
            return Menu.MenuStage.postlogin;
        }

        int gameNum;

        try {
            gameNum = Integer.parseInt(tokens[1]);
        } catch (NumberFormatException ex) {
            Menu.printError("The second argument must be an integer.");
            return Menu.MenuStage.postlogin;
        }

        if(gameNum < 1 || games == null || gameNum > games.length) {
            Menu.printError("The second argument is not a valid Game ID.");
            return Menu.MenuStage.postlogin;
        }

        gameInfo = new JoinGameRequest(null, games[gameNum-1].gameID());
        return Menu.MenuStage.inGame;
    }

    private void handleLogout() {
        try {
            facade.logout(authToken);
        }
        catch(ServiceException ignored) { }
    }
}
