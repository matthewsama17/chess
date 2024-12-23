package chess;

import java.util.Collection;
import java.util.Objects;
import chess.ChessGame.TeamColor;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return switch (type) {
            case KING -> ChessPieceMoveFinder.kingMoves(board, myPosition, pieceColor);
            case QUEEN -> ChessPieceMoveFinder.queenMoves(board, myPosition, pieceColor);
            case BISHOP -> ChessPieceMoveFinder.bishopMoves(board, myPosition, pieceColor);
            case KNIGHT -> ChessPieceMoveFinder.knightMoves(board, myPosition, pieceColor);
            case ROOK -> ChessPieceMoveFinder.rookMoves(board, myPosition, pieceColor);
            case PAWN -> ChessPieceMoveFinder.pawnMoves(board, myPosition, pieceColor);
        };
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChessPiece that)) {
            return false;
        }

        return pieceColor == that.pieceColor && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(pieceColor);
        result = 31 * result + Objects.hashCode(type);
        return result;
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }
}
