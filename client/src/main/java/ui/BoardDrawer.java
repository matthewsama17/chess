package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class BoardDrawer {
    private static String[] letters = {" a ", " b ", " c ", " d ", " e ", " f ", " g ", " h "};

    public static void main(String[] args) {
        ChessBoard board = new ChessBoard();

        draw(board);
        System.out.println();
        draw(board, ChessGame.TeamColor.BLACK);
    }

    public static void draw(ChessBoard board) {
        draw(board, ChessGame.TeamColor.WHITE);
    }

    public static void draw(ChessBoard board, ChessGame.TeamColor color) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        drawBoard(out, board, color);
    }

    private static void drawBoard(PrintStream out, ChessBoard board, ChessGame.TeamColor color) {
        drawBorder(out, color);
        for(int i = 1; i <= 8; i++) {
            int row = i;
            if(color != ChessGame.TeamColor.BLACK) {
                row = 9-i;
            }
            drawSideBorder(out, row);
            drawBoardRow(out, board, row-1, color);
            drawSideBorder(out, row);
            finishLine(out);
        }
        drawBorder(out, color);

    }

    private static void drawBorder(PrintStream out, ChessGame.TeamColor color) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);

        out.print(EMPTY);
        for(int i = 0; i < 8; i++) {
            String letter = letters[i];
            if(color == ChessGame.TeamColor.BLACK) {
                letter = letters[7-i];
            }
            out.print(letter);
        }
        out.print(EMPTY);

        finishLine(out);
    }

    private static void drawSideBorder(PrintStream out, int row) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);

        out.print(" ");
        out.print(row);
        out.print(" ");
    }

    private static void drawBoardRow(PrintStream out, ChessBoard board, int row, ChessGame.TeamColor color) {
        for(int i = 0; i < 8; i++) {
            int col = i;
            if(color == ChessGame.TeamColor.BLACK) {
                col = 7-i;
            }

            setSquareColor(out, row, col);
            out.print(EMPTY);
        }
    }

    private static void setSquareColor(PrintStream out, int row, int col) {
        if(((row + col) % 2) == 0) {
            out.print(SET_BG_COLOR_BLACK);
        }
        else {
            out.print(SET_BG_COLOR_WHITE);
        }
    }

    private static void finishLine(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
        out.println();
    }
}
