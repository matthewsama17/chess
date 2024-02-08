package chess;

import java.util.Collection;
import java.util.HashSet;
import static java.lang.Math.abs;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn = TeamColor.WHITE;
    private ChessBoard gameBoard = new ChessBoard();
    private boolean whiteCastleLeft = true;
    private boolean whiteCastleRight = true;
    private boolean blackCastleLeft = true;
    private boolean blackCastleRight = true;
    private ChessMove enPassantRight = null;
    private ChessMove enPassantLeft = null;

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
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        TeamColor color = gameBoard.getPieceColor(startPosition);
        if(color == null) {
            return null;
        }

        Collection<ChessMove> moves = gameBoard.getMoves(startPosition);
        if(enPassantLeft != null
                && startPosition.equals(enPassantLeft.getStartPosition())) { moves.add(enPassantLeft); }
        if(enPassantRight != null
                && startPosition.equals(enPassantRight.getStartPosition())) { moves.add(enPassantRight); }
        Collection<ChessMove> badMoves = new HashSet<> ();
        for(ChessMove m: moves) {
            ChessPiece originalStart = gameBoard.getPiece(m.getStartPosition());
            ChessPiece originalEnd = gameBoard.getPiece(m.getEndPosition());

            if(originalEnd != null && originalEnd.getPieceType() == ChessPiece.PieceType.KING) {
                badMoves.add(m);
                continue;
            }

            makeAnyMove(m);
            if(isInCheck(color)) { badMoves.add(m); }

            gameBoard.addPiece(m.getStartPosition(),originalStart);
            gameBoard.addPiece(m.getEndPosition(),originalEnd);
        }

        moves.removeAll(badMoves);
        moves.addAll(castle(startPosition));
        return moves;
    }

    /**
     * Gets all valid castle moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    private Collection<ChessMove> castle(ChessPosition startPosition) {
        Collection<ChessMove> castles = new HashSet<> ();

        if(startPosition.equals(new ChessPosition(1,5))
                && whiteCastleLeft
                && gameBoard.getPiece(new ChessPosition(1,5)).equals(new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.KING))
                && gameBoard.getPieceColor(new ChessPosition(1,4)) == null
                && gameBoard.getPieceColor(new ChessPosition(1,3)) == null
                && gameBoard.getPieceColor(new ChessPosition(1,2)) == null
                && gameBoard.getPiece(new ChessPosition(1,1)).equals(new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.ROOK))
                && !isInCheck(TeamColor.WHITE)) {

            makeAnyMove(new ChessMove(new ChessPosition(1,5),new ChessPosition(1,4),null));
            if(!isInCheck(TeamColor.WHITE)) {
                makeAnyMove(new ChessMove(new ChessPosition(1,4),new ChessPosition(1,3),null));
                makeAnyMove(new ChessMove(new ChessPosition(1,1),new ChessPosition(1,4),null));
                if(!isInCheck(TeamColor.WHITE)) {
                    castles.add(new ChessMove(new ChessPosition(1,5),new ChessPosition(1,3),null));
                }
                makeAnyMove(new ChessMove(new ChessPosition(1,4),new ChessPosition(1,1),null));
                makeAnyMove(new ChessMove(new ChessPosition(1,3),new ChessPosition(1,4),null));
            }
            makeAnyMove(new ChessMove(new ChessPosition(1,4),new ChessPosition(1,5),null));
        }

        if(startPosition.equals(new ChessPosition(1,5))
                && whiteCastleRight
                && gameBoard.getPiece(new ChessPosition(1,5)).equals(new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.KING))
                && gameBoard.getPieceColor(new ChessPosition(1,6)) == null
                && gameBoard.getPieceColor(new ChessPosition(1,7)) == null
                && gameBoard.getPiece(new ChessPosition(1,8)).equals(new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.ROOK))
                && !isInCheck(TeamColor.WHITE)) {

            makeAnyMove(new ChessMove(new ChessPosition(1,5),new ChessPosition(1,6),null));
            if(!isInCheck(TeamColor.WHITE)) {
                makeAnyMove(new ChessMove(new ChessPosition(1,6),new ChessPosition(1,7),null));
                makeAnyMove(new ChessMove(new ChessPosition(1,8),new ChessPosition(1,6),null));
                if(!isInCheck(TeamColor.WHITE)) {
                    castles.add(new ChessMove(new ChessPosition(1,5),new ChessPosition(1,7),null));
                }
                makeAnyMove(new ChessMove(new ChessPosition(1,6),new ChessPosition(1,8),null));
                makeAnyMove(new ChessMove(new ChessPosition(1,7),new ChessPosition(1,6),null));
            }
            makeAnyMove(new ChessMove(new ChessPosition(1,6),new ChessPosition(1,5),null));
        }

        if(startPosition.equals(new ChessPosition(8,5))
                && blackCastleLeft
                && gameBoard.getPiece(new ChessPosition(8,5)).equals(new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.KING))
                && gameBoard.getPieceColor(new ChessPosition(8,4)) == null
                && gameBoard.getPieceColor(new ChessPosition(8,3)) == null
                && gameBoard.getPieceColor(new ChessPosition(8,2)) == null
                && gameBoard.getPiece(new ChessPosition(8,1)).equals(new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.ROOK))
                && !isInCheck(TeamColor.BLACK)) {

            makeAnyMove(new ChessMove(new ChessPosition(8,5),new ChessPosition(8,4),null));
            if(!isInCheck(TeamColor.BLACK)) {
                makeAnyMove(new ChessMove(new ChessPosition(8,4),new ChessPosition(8,3),null));
                makeAnyMove(new ChessMove(new ChessPosition(8,1),new ChessPosition(8,4),null));
                if(!isInCheck(TeamColor.BLACK)) {
                    castles.add(new ChessMove(new ChessPosition(8,5),new ChessPosition(8,3),null));
                }
                makeAnyMove(new ChessMove(new ChessPosition(8,4),new ChessPosition(8,1),null));
                makeAnyMove(new ChessMove(new ChessPosition(8,3),new ChessPosition(8,4),null));
            }
            makeAnyMove(new ChessMove(new ChessPosition(8,4),new ChessPosition(8,5),null));
        }

        if(startPosition.equals(new ChessPosition(8,5))
                && blackCastleRight
                && gameBoard.getPiece(new ChessPosition(8,5)).equals(new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.KING))
                && gameBoard.getPieceColor(new ChessPosition(8,6)) == null
                && gameBoard.getPieceColor(new ChessPosition(8,7)) == null
                && gameBoard.getPiece(new ChessPosition(8,8)).equals(new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.ROOK))
                && !isInCheck(TeamColor.BLACK)) {

            makeAnyMove(new ChessMove(new ChessPosition(8,5),new ChessPosition(8,6),null));
            if(!isInCheck(TeamColor.BLACK)) {
                makeAnyMove(new ChessMove(new ChessPosition(8,6),new ChessPosition(8,7),null));
                makeAnyMove(new ChessMove(new ChessPosition(8,8),new ChessPosition(8,6),null));
                if(!isInCheck(TeamColor.BLACK)) {
                    castles.add(new ChessMove(new ChessPosition(8,5),new ChessPosition(8,7),null));
                }
                makeAnyMove(new ChessMove(new ChessPosition(8,6),new ChessPosition(8,8),null));
                makeAnyMove(new ChessMove(new ChessPosition(8,7),new ChessPosition(8,6),null));
            }
            makeAnyMove(new ChessMove(new ChessPosition(8,6),new ChessPosition(8,5),null));
        }

        return castles;
    }

    /**
     * Makes a move in a chess game regardless of whether it is legal
     *
     * @param move chess move to preform
     */
    private void makeAnyMove(ChessMove move) {
        ChessPiece.PieceType type;
        if(move.getPromotionPiece() == null) {
            type = gameBoard.getPiece(move.getStartPosition()).getPieceType();
        }
        else {
            type = move.getPromotionPiece();
        }
        TeamColor color = gameBoard.getPieceColor(move.getStartPosition());
        ChessPiece endPiece = new ChessPiece(color,type);

        gameBoard.addPiece(move.getStartPosition(),null);
        gameBoard.addPiece(move.getEndPosition(),endPiece);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        TeamColor color = gameBoard.getPieceColor(move.getStartPosition());
        if(color == null) {
            throw new InvalidMoveException();
        }
        if(teamTurn != color) {
            throw new InvalidMoveException();
        }
        Collection<ChessMove> valid = validMoves(move.getStartPosition());
        if(!valid.contains(move)) {
            throw new InvalidMoveException();
        }
        makeAnyMove(move);
        checkIfCastled(move);
        staleCastleMoves(move);
        checkIfEnPassanted(move);
        advanceTeamTurn();
        resetEnPassant(move);
    }

    /**
     * Check if the move that just happened was a castle, and move the rook appropriately
     *
     * @param move that just happened
     */
    private void checkIfCastled (ChessMove move) {
        if(whiteCastleLeft &&
                move.equals(new ChessMove(new ChessPosition(1,5),new ChessPosition(1,3),null))) {
            makeAnyMove(new ChessMove(new ChessPosition(1,1),new ChessPosition(1,4),null));
        }
        if(whiteCastleRight &&
                move.equals(new ChessMove(new ChessPosition(1,5),new ChessPosition(1,7),null))) {
            makeAnyMove(new ChessMove(new ChessPosition(1,8),new ChessPosition(1,6),null));
        }
        if(blackCastleLeft &&
                move.equals(new ChessMove(new ChessPosition(8,5),new ChessPosition(8,3),null))) {
            makeAnyMove(new ChessMove(new ChessPosition(8,1),new ChessPosition(8,4),null));
        }
        if(blackCastleRight &&
                move.equals(new ChessMove(new ChessPosition(8,5),new ChessPosition(8,7),null))) {
            makeAnyMove(new ChessMove(new ChessPosition(8,8),new ChessPosition(8,6),null));
        }
    }

    /**
     * Check if the move that just happened was an en Passant, and kills the pawn appropriately
     *
     * @param move that just happened
     */
    private void checkIfEnPassanted (ChessMove move) {
        int row = move.getStartPosition().getRow();
        int col = move.getStartPosition().getColumn();
        if(move.equals(enPassantRight)) {
            ChessPosition position = new ChessPosition(row,col+1);
            gameBoard.addPiece(position,null);
        }
        if(move.equals(enPassantLeft)) {
            ChessPosition position = new ChessPosition(row,col-1);
            gameBoard.addPiece(position,null);
        }
    }

    /**
     * Checks if this move makes future castling impossible
     *
     * @param move chess move to check
     */
    private void staleCastleMoves(ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPosition whiteKing = new ChessPosition(1,5);
        ChessPosition blackKing = new ChessPosition(8,5);
        ChessPosition whiteRookLeft = new ChessPosition(1,1);
        ChessPosition whiteRookRight = new ChessPosition(1,8);
        ChessPosition blackRookLeft = new ChessPosition(8,1);
        ChessPosition blackRookRight = new ChessPosition(8,8);

        if(end.equals(whiteKing)) {
            whiteCastleLeft = false;
            whiteCastleRight = false;
        }
        if(end.equals(blackKing)) {
            blackCastleLeft = false;
            blackCastleRight = false;
        }
        if(end.equals(whiteRookLeft)) {
            whiteCastleLeft = false;
        }
        if(end.equals(whiteRookRight)) {
            whiteCastleRight = false;
        }
        if(end.equals(blackRookLeft)) {
            blackCastleLeft = false;
        }
        if(end.equals(blackRookRight)) {
            blackCastleRight = false;
        }
    }

    /**
     * Checks if this move sets up for an En Passant
     *
     * @param move chess move to check
     */
    private void resetEnPassant(ChessMove move) {
        enPassantRight = null;
        enPassantLeft = null;

        if(gameBoard.getPiece(move.getEndPosition()).getPieceType() == ChessPiece.PieceType.PAWN
                && abs(move.getEndPosition().getRow() - move.getStartPosition().getRow()) > 1) {
            int row = move.getEndPosition().getRow();
            int skipRow = 6;
            if(row == 4) { skipRow = 3; }
            int col = move.getEndPosition().getColumn();
            if(col > 1
                    && gameBoard.getPieceColor(new ChessPosition(row,col-1)) == teamTurn
                    && gameBoard.getPiece(new ChessPosition(row,col-1)).getPieceType() == ChessPiece.PieceType.PAWN) {
                enPassantRight = new ChessMove(new ChessPosition(row,col-1),new ChessPosition(skipRow,col),null);
            }
            if(col < 8
                    && gameBoard.getPieceColor(new ChessPosition(row,col+1)) == teamTurn
                    && gameBoard.getPiece(new ChessPosition(row,col+1)).getPieceType() == ChessPiece.PieceType.PAWN) {
                enPassantLeft = new ChessMove(new ChessPosition(row,col+1),new ChessPosition(skipRow,col),null);
            }
        }
    }

    /**
     * Determines if the given team has any valid moves. Used by isInCheckmate and
     * isInStalemate.
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team has a valid move
     */
    private boolean hasValidMove(TeamColor teamColor) {
        boolean hasMove = false;
        for(int i = 1; i <= 8; i++) {
            for(int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i,j);
                if(gameBoard.getPieceColor(position) != teamColor) { continue; }
                Collection<ChessMove> moves = validMoves(position);
                if(moves == null) { continue; }
                if(moves.isEmpty()) { continue; }
                hasMove = true;
                break;
            }
            if(hasMove) { break; }
        }
        return hasMove;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKing(teamColor);
        if(kingPosition == null) { return false; }
        boolean kingInCheck = false;

        for(int i = 1; i <= 8; i++) {
            for(int j = 1; j <= 8; j++) {
                Collection<ChessMove> moves = gameBoard.getMoves(new ChessPosition(i,j));
                if(moves == null) { continue; }
                for(ChessMove m: moves) {
                    if(m.getEndPosition().equals(kingPosition)){
                        kingInCheck = true;
                        break;
                    }
                }
                if(kingInCheck) { break; }
            }
            if(kingInCheck) { break; }
        }

        return kingInCheck;
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
                if(gameBoard.getPiece(kingPosition) == null) { continue; }
                if(gameBoard.getPiece(kingPosition).equals(kingExample)) {
                    kingFound = true;
                }
                if(kingFound) { break; }
            }
            if(kingFound) { break; }
        }
        if(!kingFound) { kingPosition = null; }

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
