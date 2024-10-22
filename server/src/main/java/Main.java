import chess.*;
import com.google.gson.Gson;
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

        Server server = new Server();
        server.run(8080);
    }
}