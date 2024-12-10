package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;
import java.util.Collection;
import java.util.HashSet;

public class BoardDrawer {
    private static String[] letters = {" A ", " B ", " C ", " D ", " E ", " F ", " G ", " H "};

    public static void main(String[] args) {
        ChessBoard board = new ChessBoard();
        board.resetBoard();

        draw(board);
        System.out.println();

        board.movePiece(new ChessMove(new ChessPosition(2, 5), new ChessPosition(4, 5), null));
        draw(board, ChessGame.TeamColor.BLACK);
        System.out.println();

        board.movePiece(new ChessMove(new ChessPosition(7, 5), new ChessPosition(5, 5), null));
        board.movePiece(new ChessMove(new ChessPosition(1, 4), new ChessPosition(3, 6), null));
        board.movePiece(new ChessMove(new ChessPosition(8, 4), new ChessPosition(6, 6), null));
        ChessPosition startPosition = new ChessPosition(3, 6);
        drawMoves(board, ChessGame.TeamColor.WHITE, board.getPieceMoves(startPosition), startPosition);
        System.out.println();

        startPosition = new ChessPosition(6, 6);
        drawMoves(board, ChessGame.TeamColor.BLACK, board.getPieceMoves(startPosition), startPosition);
    }

    public static void drawMoves(ChessBoard board, ChessGame.TeamColor color, Collection<ChessMove> moves, ChessPosition startPosition) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        Collection<ChessPosition> endPositions = new HashSet<>();
        if(moves != null) {
            for (ChessMove move : moves) {
                endPositions.add(move.getEndPosition());
            }
        }

        drawBoard(out, board, color, endPositions, startPosition);
    }

    public static void draw(ChessBoard board, ChessGame.TeamColor color) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        Collection<ChessPosition> endPositions = new HashSet<>();

        drawBoard(out, board, color, endPositions, null);
    }

    public static void draw(ChessBoard board) {
        draw(board, ChessGame.TeamColor.WHITE);
    }

    public static void draw(ChessGame.TeamColor color) {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        draw(board, color);
    }

    public static void draw() {
        draw(ChessGame.TeamColor.WHITE);
    }

    private static void drawBoard(PrintStream out, ChessBoard board, ChessGame.TeamColor color,
                                  Collection<ChessPosition> endPositions, ChessPosition startPosition) {
        drawBorder(out, color, true);
        for(int i = 1; i <= 8; i++) {
            int row = i;
            if(color != ChessGame.TeamColor.BLACK) {
                row = 9-i;
            }
            drawSideBorder(out, row);
            drawBoardRow(out, board, row, color, endPositions, startPosition);
            drawSideBorder(out, row);
            finishLine(out);
        }
        drawBorder(out, color, false);

    }

    private static void drawBorder(PrintStream out, ChessGame.TeamColor color, boolean top) {
        ChessGame.TeamColor cornerColor = color;
        if(top) {
            cornerColor = (color == ChessGame.TeamColor.BLACK) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
        }
        drawCorner(out, cornerColor);

        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);

        for(int i = 0; i < 8; i++) {
            String letter = letters[i];
            if(color == ChessGame.TeamColor.BLACK) {
                letter = letters[7-i];
            }
            out.print(letter);
        }

        drawCorner(out, cornerColor);
        finishLine(out);
    }

    private static void drawCorner(PrintStream out, ChessGame.TeamColor color) {
        if(color == ChessGame.TeamColor.BLACK) {
            out.print(SET_BG_COLOR_MAGENTA);
        }
        else {
            out.print(SET_BG_COLOR_GREEN);
        }
        out.print(EMPTY);
    }

    private static void drawSideBorder(PrintStream out, int row) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);

        out.print(" ");
        out.print(row);
        out.print(" ");
    }

    private static void drawBoardRow(PrintStream out, ChessBoard board, int row, ChessGame.TeamColor color,
                                     Collection<ChessPosition> endPositions, ChessPosition startPosition) {
        for(int i = 1; i <= 8; i++) {
            int col = i;
            if(color == ChessGame.TeamColor.BLACK) {
                col = 9-i;
            }

            setSquareColor(out, row, col, endPositions);
            drawPiece(out, board, row, col, endPositions, startPosition);
        }
    }

    private static void setSquareColor(PrintStream out, int row, int col, Collection<ChessPosition> endPositions) {
        if(((row + col) % 2) == 0) {
            out.print(SET_BG_COLOR_BLACK);
            if(endPositions.contains(new ChessPosition(row, col))) {
                out.print(SET_BG_COLOR_DARK_YELLOW);
            }
        }
        else {
            out.print(SET_BG_COLOR_WHITE);
            if(endPositions.contains(new ChessPosition(row, col))) {
                out.print(SET_BG_COLOR_YELLOW);
            }
        }


    }

    private static void drawPiece(PrintStream out, ChessBoard board, int row, int col,
                                  Collection<ChessPosition> endPositions, ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(new ChessPosition(row, col));

        String pieceString = null;
        if(piece == null) {
            pieceString = EMPTY;
        }
        else if(piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            out.print(SET_TEXT_COLOR_MAGENTA);

            if(new ChessPosition(row, col).equals(startPosition)) {
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(SET_BG_COLOR_MAGENTA);
            }

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

            if(new ChessPosition(row, col).equals(startPosition)) {
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(SET_BG_COLOR_GREEN);
            }

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

        ChessPosition position = new ChessPosition(row, col);
        if(position.equals(startPosition) || endPositions.contains(position)) {
            out.print(SET_TEXT_COLOR_BLACK);
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
