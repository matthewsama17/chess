package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class BoardDrawer {
    private static String[] letters = {" A ", " B ", " C ", " D ", " E ", " F ", " G ", " H "};

    public static void main(String[] args) {
        ChessBoard board = new ChessBoard();
        board.resetBoard();

        draw(board);
        System.out.println();
        board.movePiece(new ChessMove(new ChessPosition(2, 5), new ChessPosition(4, 5), null));
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
            drawBoardRow(out, board, row, color);
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
        for(int i = 1; i <= 8; i++) {
            int col = i;
            if(color == ChessGame.TeamColor.BLACK) {
                col = 9-i;
            }

            setSquareColor(out, row, col);
            drawPiece(out, board, row, col);
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

    private static void drawPiece(PrintStream out, ChessBoard board, int row, int col) {
        ChessPiece piece = board.getPiece(new ChessPosition(row, col));

        String pieceString = null;
        if(piece == null) {
            pieceString = EMPTY;
        }
        else if(piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            out.print(SET_TEXT_COLOR_MAGENTA);

            if(piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                pieceString = BLACK_PAWN;
            }
            else if(piece.getPieceType() == ChessPiece.PieceType.ROOK) {
                pieceString = BLACK_ROOK;
            }
            else if(piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                pieceString = BLACK_KNIGHT;
            }
            else if(piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                pieceString = BLACK_BISHOP;
            }
            else if(piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
                pieceString = BLACK_QUEEN;
            }
            else if(piece.getPieceType() == ChessPiece.PieceType.KING) {
                pieceString = BLACK_KING;
            }
        }
        else {
            out.print(SET_TEXT_COLOR_GREEN);

            if(piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                pieceString = WHITE_PAWN;
            }
            else if(piece.getPieceType() == ChessPiece.PieceType.ROOK) {
                pieceString = WHITE_ROOK;
            }
            else if(piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                pieceString = WHITE_KNIGHT;
            }
            else if(piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                pieceString = WHITE_BISHOP;
            }
            else if(piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
                pieceString = WHITE_QUEEN;
            }
            else if(piece.getPieceType() == ChessPiece.PieceType.KING) {
                pieceString = WHITE_KING;
            }
        }

        out.print(SET_TEXT_BOLD);
        out.print(pieceString);
    }

    private static void finishLine(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
        out.println();
    }
}
