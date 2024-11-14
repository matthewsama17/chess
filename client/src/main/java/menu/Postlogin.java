package menu;

import serverfacade.ServerFacade;

public class Postlogin {
    ServerFacade facade;

    public Postlogin(ServerFacade facade) {
        this.facade = facade;
    }

    public Menu.MenuStage eval(String input, String authToken) {
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
        System.out.println("creation");
        return Menu.MenuStage.postlogin;
    }

    private Menu.MenuStage handleList() {
        System.out.println("(Pretend this is a list of a bunch of games)");
        return Menu.MenuStage.postlogin;
    }

    private Menu.MenuStage handleJoin(String[] tokens) {
        System.out.println("Join a fun game!");
        return Menu.MenuStage.postlogin;
    }

    private Menu.MenuStage handleObserve(String[] tokens) {
        System.out.println("Observe");
        return Menu.MenuStage.postlogin;
    }

    private void handleLogout() {
        System.out.println("logout");
    }
}
