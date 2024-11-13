package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class BoardDrawer {
    private static String[] letters = {" a ", " b ", " c ", " d ", " e ", " f ", " g ", " h "};

    public static void main(String[] args) {
        ChessBoard board = new ChessBoard();

        draw(board, ChessGame.TeamColor.BLACK);
    }

    public static void draw(ChessBoard board, ChessGame.TeamColor color) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        drawBorder(out, color);
        out.print(SET_TEXT_COLOR_YELLOW);
        out.print(SET_BG_COLOR_BLUE);
        out.print("Board goes heve lol");
        finishLine(out);
        drawBorder(out, color);
    }

    public static void draw(ChessBoard board) {
        draw(board, ChessGame.TeamColor.WHITE);
    }

    private static void drawBorder(PrintStream out, ChessGame.TeamColor color) {
        boolean backwards = (color == ChessGame.TeamColor.BLACK);

        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);

        out.print(EMPTY);
        for(int i = 0; i < 8; i++) {
            if(backwards) {
                out.print(letters[7-i]);
            }
            else {
                out.print(letters[i]);
            }
        }
        out.print(EMPTY);

        finishLine(out);
    }

    private static void finishLine(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
        out.println();
    }
}
