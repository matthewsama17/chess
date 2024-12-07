package menu;

import request.*;
import result.*;
import serverfacade.ServerFacade;

public class Prelogin {
    ServerFacade facade;

    public Prelogin(ServerFacade facade) {
        this.facade = facade;
    }

    public LoginResult eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");

        if(tokens.length == 0) {
            printHelp();
            return null;
        }
        else if(tokens[0].equals("register")) {
            return handleRegister(tokens);
        }
        else if(tokens[0].equals("login")) {
            return handleLogin(tokens);
        }
        else if(tokens[0].equals("quit")) {
            System.out.println("Quitting...");
            return null;
        }
        else {
            printHelp();
            return null;
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

    private LoginResult handleRegister(String[] tokens) {
        if(tokens.length != 4) {
            Menu.printError("Wrong number of arguments. Request could not be processed.");
            return null;
        }

        RegisterRequest registerRequest =  new RegisterRequest(tokens[1], tokens[2], tokens[3]);
        LoginResult loginResult;
        try {
            loginResult = facade.register(registerRequest);
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
            return null;
        }

        System.out.println("Registered successfully!");
        return loginResult;
    }

    private LoginResult handleLogin(String[] tokens) {
        if(tokens.length != 3) {
            Menu.printError("Wrong number of arguments. Request could not be processed.");
            return null;
        }

        LoginRequest loginRequest = new LoginRequest(tokens[1], tokens[2]);
        LoginResult loginResult;
        try {
            loginResult = facade.login(loginRequest);
        }
        catch(ServiceException ex) {
            if(ex.getStatusCode() == 401) {
                Menu.printError("The username or password was incorrect.");
            }
            else {
                Menu.printError();
            }
            return null;
        }

        System.out.println("Logged in successfully!");
        return loginResult;
    }
}
