package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn = TeamColor.WHITE;
    private ChessBoard gameBoard = new ChessBoard();

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    public void updateTeamTurn() {
        if(teamTurn == TeamColor.WHITE) {
            teamTurn = TeamColor.BLACK;
        }
        if(teamTurn == TeamColor.BLACK) {
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
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if(validMoves == null || !validMoves.contains(move)) {
            throw new InvalidMoveException();
        }

        gameBoard.movePiece(move);
        updateTeamTurn();
    }

    /**
     * returns the location of the given team's king
     *
     * @param teamColor which king to find
     * @return the position of the king of the given team
     */
    private ChessPosition findKing(TeamColor teamColor) {
        for(int i = 1; i <= 8; i++) {
            for(int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i,j);
                ChessPiece piece = gameBoard.getPiece(position);
                if(piece.getPieceType() == ChessPiece.PieceType.KING
                        && piece.getTeamColor() == TeamColor.WHITE) {
                    return position;
                }
            }
        }
        return new ChessPosition(0,0);
    }

    /**
     * returns all moves that can be made by a piece on the board
     * Does not take into account moves that are illegal due to leaving the king
     * in danger
     *
     * @return Collection of all ChessMoves possible
     */
    private Collection<ChessMove> allMoves() {
        Collection<ChessMove> allMoves = new HashSet<>();

        for(int i = 1; i <= 8; i++) {
            for(int j = 1; j <= 8; j++) {

                Collection<ChessMove> moves = gameBoard.getPieceMoves(new ChessPosition(i,j));
                if(moves != null) {
                    allMoves.addAll(moves);
                }
            }
        }

        return allMoves;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKing(teamColor);
        Collection<ChessMove> allMoves = allMoves();

        for(ChessMove move : allMoves) {
            if(move.getEndPosition() == kingPosition) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team has no valid moves
     *
     * @param teamColor which team to check for valid moves
     * @return True if the specified team has no valid moves
     */
    private boolean hasNoMoves(TeamColor teamColor) {
        boolean thereAreNoMoves = true;

        for(int i = 1; i <= 8; i++) {
            for(int j = 1; j <= 8; j++) {

                ChessPosition position = new ChessPosition(i,j);
                TeamColor positionColor = gameBoard.getPieceColor(position);
                if(positionColor == teamColor) {

                    Collection<ChessMove> moves = validMoves(position);
                    if(moves != null && !moves.isEmpty()) {
                        thereAreNoMoves = false;
                    }
                }
            }
        }

        return thereAreNoMoves;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && hasNoMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && hasNoMoves(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessGame chessGame)) return false;

        return teamTurn == chessGame.teamTurn && Objects.equals(gameBoard, chessGame.gameBoard);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(teamTurn);
        result = 31 * result + Objects.hashCode(gameBoard);
        return result;
    }
}
