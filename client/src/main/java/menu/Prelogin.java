package menu;

public class Prelogin {
    public static Menu.MenuStage eval(String input) {
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

    public static void printHelp() {
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

    private static Menu.MenuStage handleRegister(String[] tokens) {
        System.out.println("I see you are trying to register!");
        System.out.println("Let's assume you are successful");
        return Menu.MenuStage.postlogin;
    }

    private static Menu.MenuStage handleLogin(String[] tokens) {
        System.out.println("I see you are trying to login!");
        System.out.println("Let's assume you are successful");
        return Menu.MenuStage.postlogin;
    }
}
