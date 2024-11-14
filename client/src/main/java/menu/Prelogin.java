package menu;

import request.*;
import result.*;
import serverfacade.ServerFacade;

public class Prelogin {
    ServerFacade facade;
    private String authToken = null;
    private String username = null;

    public Prelogin(ServerFacade facade) {
        this.facade = facade;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUsername() {
        return username;
    }

    public Menu.MenuStage eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");

        if(tokens.length == 0) {
            printHelp();
            return Menu.MenuStage.prelogin;
        }
        else if(tokens[0].equals("register")) {
            return handleRegister(tokens);
        }
        else if(tokens[0].equals("login")) {
            return handleLogin(tokens);
        }
        else if(tokens[0].equals("quit")) {
            System.out.println("Quitting...");
            return Menu.MenuStage.prelogin;
        }
        else {
            printHelp();
            return Menu.MenuStage.prelogin;
        }
    }

    public void printHelp() {
        System.out.println("Commands:");
        Menu.printCommand("help");
        System.out.println(" - Display these commands");
        Menu.printCommand("register <USERNAME> <PASSWORD> <EMAIL>");
        System.out.println(" - Create a new account");
        Menu.printCommand("login <USERNAME> <PASSWORD>");
        System.out.println(" - Enter with a preexisting account");
        Menu.printCommand("quit");
        System.out.println(" - End this program");
    }

    private Menu.MenuStage handleRegister(String[] tokens) {
        if(tokens.length != 4) {
            Menu.printError("Wrong number of arguments. Request could not be processed.");
            return Menu.MenuStage.prelogin;
        }

        RegisterRequest registerRequest =  new RegisterRequest(tokens[1], tokens[2], tokens[3]);
        try {
            LoginResult loginResult = facade.register(registerRequest);
            authToken = loginResult.authToken();
            username = loginResult.username();
        }
        catch(ServiceException ex) {
            if(ex.getStatusCode() == 400) {
                Menu.printError("Request could not be processed.");
            }
            else if(ex.getStatusCode() == 403) {
                Menu.printError("That username is already taken.");
            }
            else {
                Menu.printError();
            }
            return Menu.MenuStage.prelogin;
        }

        System.out.println("Registered successfully!");
        return Menu.MenuStage.postlogin;
    }

    private Menu.MenuStage handleLogin(String[] tokens) {
        if(tokens.length != 3) {
            Menu.printError("Wrong number of arguments. Request could not be processed.");
            return Menu.MenuStage.prelogin;
        }

        LoginRequest loginRequest = new LoginRequest(tokens[1], tokens[2]);
        try {
            LoginResult loginResult = facade.login(loginRequest);
            authToken = loginResult.authToken();
            username = loginResult.username();
        }
        catch(ServiceException ex) {
            if(ex.getStatusCode() == 401) {
                Menu.printError("The username or password was incorrect.");
            }
            else {
                Menu.printError();
            }
            return Menu.MenuStage.prelogin;
        }

        System.out.println("Logged in successfully!");
        return Menu.MenuStage.postlogin;
    }
}
