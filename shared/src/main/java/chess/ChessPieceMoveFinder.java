package chess;

import java.util.HashSet;

public class ChessPieceMoveFinder {

    /**
     * @return whether the given position is on a 8X8 chess board
     */
    public static boolean isOnBoard(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        return 0 < row && row <= 8 && 0 < col && col <= 8;
    }

    /**
     * Calculates all the positions a pawn can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public static HashSet<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Calculates all the positions a king can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public static HashSet<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Calculates all the positions a knight can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public static HashSet<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Calculates all the positions a rook can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public static HashSet<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Calculates all the positions a bishop can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public static HashSet<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Calculates all the positions a knight can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public static HashSet<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {
        HashSet<ChessMove> moves = new HashSet<ChessMove>();
        moves.addAll(bishopMoves(board, myPosition, myColor));
        moves.addAll(rookMoves(board, myPosition, myColor));
        return moves;
    }
}