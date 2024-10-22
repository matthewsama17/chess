import chess.*;
import com.google.gson.Gson;
import dataaccess.UserDAO;
import dataaccess.memory.MemoryUserDAO;
import model.UserData;
import server.Server;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

        Gson gson = new Gson();
        UserData userData = new UserData("Matthew", "goodPassWord", "mgh57@byu.edu");
        String jsonString = gson.toJson(userData);
        System.out.println(jsonString);
        UserData jsonData = gson.fromJson(jsonString, UserData.class);
        System.out.println("username is " + jsonData.username() + ", password is " + jsonData.password());

        UserDAO userDAO = new MemoryUserDAO();
        userDAO.createUser(jsonData);
        System.out.println(userDAO.getUser("Matthew").password());
        System.out.println(userDAO.getUser("Something Else"));
        userDAO.clear();
        System.out.println(userDAO.getUser("Matthew"));

        Server server = new Server();
        server.run(8080);
    }
}