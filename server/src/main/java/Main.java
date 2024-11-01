import chess.*;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import server.Server;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

        Gson gson = new Gson();
        String hashedPassword = BCrypt.hashpw("goodPassWord", BCrypt.gensalt());
        UserData userData = new UserData("Matthew", hashedPassword, "mgh57@byu.edu");
        String jsonString = gson.toJson(userData);
        System.out.println(jsonString);
        UserData jsonData = gson.fromJson(jsonString, UserData.class);
        System.out.println("username is " + jsonData.username() + ", passwordHash is " + jsonData.passwordHash());

        UserDAO userDAO = new MemoryUserDAO();
        try {
            userDAO.createUser(jsonData);
        } catch (DataAccessException ex ) { }

        System.out.println(userDAO.getUser("Matthew").passwordHash());
        System.out.println(userDAO.getUser("Something Else"));
        userDAO.clear();
        System.out.println(userDAO.getUser("Matthew"));

        AuthDAO authDAO = new MemoryAuthDAO();
        AuthData authData = new AuthData("234", "Matthew");
        try {
            authDAO.createAuth(authData);
        } catch (DataAccessException ex) { }
        System.out.println(authDAO.getAuth("234").username());
        System.out.println(authDAO.getAuth("Something else"));
        authDAO.deleteAuth(authData);
        System.out.println(authDAO.getAuth("234"));

        Server server = new Server();
        server.run(8080);
    }
}