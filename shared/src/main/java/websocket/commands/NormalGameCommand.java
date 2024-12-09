package websocket.commands;

//I need this because of Gson
public class NormalGameCommand extends UserGameCommand{
    public NormalGameCommand(CommandType commandType, String authToken, Integer gameID) {
        super(commandType, authToken, gameID);
    }
}
