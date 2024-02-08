package chess;

import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn = TeamColor.WHITE;
    private ChessBoard gameBoard = new ChessBoard();

    public ChessGame() { }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() { return teamTurn; }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) { teamTurn = team; }

    /**
     * Switches the team whose turn it is
     */
    public void advanceTeamTurn() {
        if(teamTurn == TeamColor.WHITE) {
            teamTurn = TeamColor.BLACK;
        }
        else if(teamTurn == TeamColor.BLACK) {
            teamTurn = TeamColor.WHITE;
        }
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team has any valid moves. Used by isInCheckmate and
     * isInStalemate.
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team has a valid move
     */
    private boolean hasValidMove(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

    }

    /**
     * Finds the position of the king. Used by isInCheck
     *
     * @param teamColor which team's king
     * @return ChessPosition of the king
     */
    private ChessPosition findKing(TeamColor teamColor) {
        boolean kingFound = false;
        ChessPiece kingExample = new ChessPiece(teamColor,ChessPiece.PieceType.KING);
        ChessPosition kingPosition = null;

        for(int i = 1; i <= 8; i++) {
            for(int j = 1; j <= 8; j++) {
                kingPosition = new ChessPosition(i,j);
                if(gameBoard.getPiece(kingPosition).equals(kingExample)) {
                    kingFound = true;
                }
                if(kingFound) { break; }
            }
            if(kingFound) { break; }
        }

        return kingPosition;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) { return isInCheck(teamColor) && !hasValidMove(teamColor); }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) { return !isInCheck(teamColor) && !hasValidMove(teamColor); }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) { gameBoard = board; }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() { return gameBoard; }
}
