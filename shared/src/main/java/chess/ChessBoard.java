package chess;

import java.util.Arrays;
import java.util.Collection;
import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Cloneable {

    ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() { }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1]  = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Gets the color of a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the color of the piece at the position, or null if no
     * piece is at that position
     */
    public TeamColor getPieceColor(ChessPosition position) {
        ChessPiece piece = getPiece(position);
        if(piece == null) {
            return null;
        }
        return piece.getTeamColor();
    }

    /**
     * Calculates all the positions the chess piece at the position can move to
     * Does not take into account moves that are illegal due to leaving the king
     * in danger
     *
     * @param position the position to get the piece from
     * @return Collection of valid moves for the piece, or null if no piece is at
     * that position
     */
    public Collection<ChessMove> getPieceMoves(ChessPosition position) {
        ChessPiece piece = getPiece(position);
        if(piece == null) {
            return null;
        }
        return piece.pieceMoves(this, position);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        //White side
        squares[0][0] = new ChessPiece(TeamColor.WHITE, PieceType.ROOK);
        squares[0][1] = new ChessPiece(TeamColor.WHITE, PieceType.KNIGHT);
        squares[0][2] = new ChessPiece(TeamColor.WHITE, PieceType.BISHOP);
        squares[0][3] = new ChessPiece(TeamColor.WHITE, PieceType.QUEEN);
        squares[0][4] = new ChessPiece(TeamColor.WHITE, PieceType.KING);
        squares[0][5] = new ChessPiece(TeamColor.WHITE, PieceType.BISHOP);
        squares[0][6] = new ChessPiece(TeamColor.WHITE, PieceType.KNIGHT);
        squares[0][7] = new ChessPiece(TeamColor.WHITE, PieceType.ROOK);

        //Black side
        squares[7][0] = new ChessPiece(TeamColor.BLACK, PieceType.ROOK);
        squares[7][1] = new ChessPiece(TeamColor.BLACK, PieceType.KNIGHT);
        squares[7][2] = new ChessPiece(TeamColor.BLACK, PieceType.BISHOP);
        squares[7][3] = new ChessPiece(TeamColor.BLACK, PieceType.QUEEN);
        squares[7][4] = new ChessPiece(TeamColor.BLACK, PieceType.KING);
        squares[7][5] = new ChessPiece(TeamColor.BLACK, PieceType.BISHOP);
        squares[7][6] = new ChessPiece(TeamColor.BLACK, PieceType.KNIGHT);
        squares[7][7] = new ChessPiece(TeamColor.BLACK, PieceType.ROOK);

        //Pawns
        for(int i = 0; i < 8; i++) {
            squares[1][i] = new ChessPiece(TeamColor.WHITE, PieceType.PAWN);
            squares[6][i] = new ChessPiece(TeamColor.BLACK, PieceType.PAWN);
        }

        //No Man's Land
        for(int i = 0; i < 8; i++) {
            for(int j = 2; j < 6; j++) {
                squares[j][i] = null;
            }
        }
    }

    @Override
    public ChessBoard clone() {
        try {
            ChessBoard clone = (ChessBoard) super.clone();
            clone.squares = this.squares.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessBoard that)) return false;

        return Arrays.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }
}
