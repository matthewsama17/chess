package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.lang.Math;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn = TeamColor.WHITE;
    private ChessBoard gameBoard;

    private Castler castler;
    private EnPassanter enPassanter;

    public ChessGame() {
        gameBoard = new ChessBoard();
        gameBoard.resetBoard();

        castler = new Castler();
        enPassanter = new EnPassanter();
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
        TeamColor pieceColor = gameBoard.getPieceColor(startPosition);
        if(pieceColor == null) {
            return null;
        }

        Collection<ChessMove> moves;
        moves = gameBoard.getPieceMoves(startPosition);
        moves.addAll(enPassanter.getEnPassantMoves(startPosition));
        moves.addAll(castler.validCastleMoves(startPosition));

        Collection<ChessMove> invalidMoves = new HashSet<ChessMove>();
        for(ChessMove move : moves) {
            ChessBoard trueBoard = gameBoard.clone();

            gameBoard.movePiece(move);
            if(isInCheck(pieceColor)) {
                invalidMoves.add(move);
            }

            gameBoard = trueBoard;
        }

        moves.removeAll(invalidMoves);
        return moves;
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

        if(gameBoard.getPieceColor(move.getStartPosition()) != teamTurn) {
            throw new InvalidMoveException();
        }

        if(enPassanter.getEnPassantMoves(move.getStartPosition()).contains(move)) {
            enPassanter.executeEnPassant(move);
        }
        if(castler.validCastleMoves(move.getStartPosition()).contains(move)) {
            castler.executeCastle(move);
        }

        gameBoard.movePiece(move);
        enPassanter.checkEnPassantOpportunities(move);
        castler.checkIfKingOrRookMoved(move);
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
                if(piece != null
                        && piece.getPieceType() == ChessPiece.PieceType.KING
                        && piece.getTeamColor() == teamColor) {
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
            if(move.getEndPosition().equals(kingPosition)) {
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
        castler = new Castler();
        enPassanter = new EnPassanter();
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
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChessGame chessGame)) {
            return false;
        }

        return teamTurn == chessGame.teamTurn && Objects.equals(gameBoard, chessGame.gameBoard);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(teamTurn);
        result = 31 * result + Objects.hashCode(gameBoard);
        return result;
    }

    /**
     * This class manages the data related to castling.
     */
    private class Castler {

        public final static ChessPiece WHITE_KING = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        public final static ChessPiece BLACK_KING = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        public final static ChessPiece WHITE_ROOK = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        public final static ChessPiece BLACK_ROOK = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);

        public final static ChessPosition WHITE_KING_START = new ChessPosition(1,5);
        public final static ChessPosition BLACK_KING_START = new ChessPosition(8,5);
        public final static ChessPosition WHITE_LEFT_ROOK_START = new ChessPosition(1,1);
        public final static ChessPosition WHITE_RIGHT_ROOK_START = new ChessPosition(1,8);
        public final static ChessPosition BLACK_LEFT_ROOK_START = new ChessPosition(8,1);
        public final static ChessPosition BLACK_RIGHT_ROOK_START = new ChessPosition(8,8);

        public final static ChessMove WHITE_LEFT_CASTLE = new ChessMove(WHITE_KING_START, new ChessPosition(1,3), null);
        public final static ChessMove WHITE_RIGHT_CASTLE = new ChessMove(WHITE_KING_START, new ChessPosition(1,7), null);
        public final static ChessMove BLACK_LEFT_CASTLE = new ChessMove(BLACK_KING_START, new ChessPosition(8,3), null);
        public final static ChessMove BLACK_RIGHT_CASTLE = new ChessMove(BLACK_KING_START, new ChessPosition(8,7), null);

        private boolean whiteKingHasMoved = false;
        private boolean blackKingHasMoved = false;
        private boolean whiteLeftRookHasMoved = false;
        private boolean whiteRightRookHasMoved = false;
        private boolean blackLeftRookHasMoved = false;
        private boolean blackRightRookHasMoved = false;

        /**
         * Updates Castler variables based on whether the given move moved a king or
         * rook out of its starting position.
         *
         * @param move the move to check
         */
        public void checkIfKingOrRookMoved(ChessMove move) {
            if(move.getStartPosition().equals(WHITE_KING_START)) {
                whiteKingHasMoved = true;
            }
            if(move.getStartPosition().equals(BLACK_KING_START)) {
                blackKingHasMoved = true;
            }
            if(move.getStartPosition().equals(WHITE_LEFT_ROOK_START)) {
                whiteLeftRookHasMoved = true;
            }
            if(move.getStartPosition().equals(WHITE_RIGHT_ROOK_START)) {
                whiteRightRookHasMoved = true;
            }
            if(move.getStartPosition().equals(BLACK_LEFT_ROOK_START)) {
                blackLeftRookHasMoved = true;
            }
            if(move.getStartPosition().equals(BLACK_RIGHT_ROOK_START)) {
                blackRightRookHasMoved = true;
            }
        }

        /**
         * Gets valid castle moves for the piece at the given location
         *
         * @param startPosition the position of the piece to get moves for
         * @return a collection of valid castle moves for the piece
         */
        public Collection<ChessMove> validCastleMoves(ChessPosition startPosition) {
            if(startPosition.equals(WHITE_KING_START)
                    && !whiteKingHasMoved
                    && ChessGame.this.gameBoard.getPiece(WHITE_KING_START) != null
                    && ChessGame.this.gameBoard.getPiece(WHITE_KING_START).equals(WHITE_KING)) {

                return validWhiteCastleMoves();
            }

            if(startPosition.equals(BLACK_KING_START)
                    && !blackKingHasMoved
                    && ChessGame.this.gameBoard.getPiece(BLACK_KING_START) != null
                    && ChessGame.this.gameBoard.getPiece(BLACK_KING_START).equals(BLACK_KING)) {
                return validBlackCastleMoves();
            }

            return new HashSet<>();
        }

        /**
         * Gets valid castle moves for the white king
         * Assumes white king has not moved
         *
         * @return a collection of valid castle moves for the white king
         */
        private Collection<ChessMove> validWhiteCastleMoves() {
            Collection<ChessMove> moves = new HashSet<ChessMove>();

            if(!whiteLeftRookHasMoved
                    && ChessGame.this.gameBoard.getPiece(WHITE_LEFT_ROOK_START) != null
                    && ChessGame.this.gameBoard.getPiece(WHITE_LEFT_ROOK_START).equals(WHITE_ROOK)
                    && ChessGame.this.gameBoard.getPiece(new ChessPosition(1,2)) == null
                    && ChessGame.this.gameBoard.getPiece(new ChessPosition(1,3)) == null
                    && ChessGame.this.gameBoard.getPiece(new ChessPosition(1,4)) == null) {
                boolean canCastle = true;
                ChessBoard trueBoard = ChessGame.this.gameBoard.clone();

                if(isInCheck(TeamColor.WHITE)) {
                    canCastle = false;
                }
                ChessGame.this.gameBoard.movePiece(new ChessMove(WHITE_KING_START, new ChessPosition(1,4), null));
                if(isInCheck(TeamColor.WHITE)) {
                    canCastle = false;
                }
                ChessGame.this.gameBoard.movePiece(new ChessMove(new ChessPosition(1,4), new ChessPosition(1,3), null));
                if(isInCheck(TeamColor.WHITE)) {
                    canCastle = false;
                }

                if(canCastle) {
                    moves.add(WHITE_LEFT_CASTLE);
                }
                ChessGame.this.gameBoard = trueBoard;
            }

            if(!whiteRightRookHasMoved
                    && ChessGame.this.gameBoard.getPiece(WHITE_RIGHT_ROOK_START) != null
                    && ChessGame.this.gameBoard.getPiece(WHITE_RIGHT_ROOK_START).equals(WHITE_ROOK)
                    && ChessGame.this.gameBoard.getPiece(new ChessPosition(1,6)) == null
                    && ChessGame.this.gameBoard.getPiece(new ChessPosition(1,7)) == null) {
                boolean canCastle = true;
                ChessBoard trueBoard = ChessGame.this.gameBoard.clone();

                if(isInCheck(TeamColor.WHITE)) {
                    canCastle = false;
                }
                ChessGame.this.gameBoard.movePiece(new ChessMove(WHITE_KING_START, new ChessPosition(1,6), null));
                if(isInCheck(TeamColor.WHITE)) {
                    canCastle = false;
                }
                ChessGame.this.gameBoard.movePiece(new ChessMove(new ChessPosition(1,6), new ChessPosition(1,7), null));
                if(isInCheck(TeamColor.WHITE)) {
                    canCastle = false;
                }

                if(canCastle) {
                    moves.add(WHITE_RIGHT_CASTLE);
                }
                ChessGame.this.gameBoard = trueBoard;
            }

            return moves;
        }

        /**
         * Gets valid castle moves for the black king
         * Assumes black king has not moved
         *
         * @return a collection of valid castle moves for the black king
         */
        private Collection<ChessMove> validBlackCastleMoves() {
            Collection<ChessMove> moves = new HashSet<ChessMove>();

            if(!blackLeftRookHasMoved
                    && ChessGame.this.gameBoard.getPiece(BLACK_LEFT_ROOK_START) != null
                    && ChessGame.this.gameBoard.getPiece(BLACK_LEFT_ROOK_START).equals(BLACK_ROOK)
                    && ChessGame.this.gameBoard.getPiece(new ChessPosition(8,2)) == null
                    && ChessGame.this.gameBoard.getPiece(new ChessPosition(8,3)) == null
                    && ChessGame.this.gameBoard.getPiece(new ChessPosition(8,4)) == null) {
                boolean canCastle = true;
                ChessBoard trueBoard = ChessGame.this.gameBoard.clone();

                if(isInCheck(TeamColor.BLACK)) {
                    canCastle = false;
                }
                ChessGame.this.gameBoard.movePiece(new ChessMove(BLACK_KING_START, new ChessPosition(8,4), null));
                if(isInCheck(TeamColor.BLACK)) {
                    canCastle = false;
                }
                ChessGame.this.gameBoard.movePiece(new ChessMove(new ChessPosition(8,4), new ChessPosition(8,3), null));
                if(isInCheck(TeamColor.BLACK)) {
                    canCastle = false;
                }

                if(canCastle) {
                    moves.add(BLACK_LEFT_CASTLE);
                }
                ChessGame.this.gameBoard = trueBoard;
            }

            if(!blackRightRookHasMoved
                    && ChessGame.this.gameBoard.getPiece(BLACK_RIGHT_ROOK_START) != null
                    && ChessGame.this.gameBoard.getPiece(BLACK_RIGHT_ROOK_START).equals(BLACK_ROOK)
                    && ChessGame.this.gameBoard.getPiece(new ChessPosition(8,6)) == null
                    && ChessGame.this.gameBoard.getPiece(new ChessPosition(8,7)) == null) {
                boolean canCastle = true;
                ChessBoard trueBoard = ChessGame.this.gameBoard.clone();

                if(isInCheck(TeamColor.BLACK)) {
                    canCastle = false;
                }
                ChessGame.this.gameBoard.movePiece(new ChessMove(BLACK_KING_START, new ChessPosition(8,6), null));
                if(isInCheck(TeamColor.BLACK)) {
                    canCastle = false;
                }
                ChessGame.this.gameBoard.movePiece(new ChessMove(new ChessPosition(8,6), new ChessPosition(8,7), null));
                if(isInCheck(TeamColor.BLACK)) {
                    canCastle = false;
                }

                if(canCastle) {
                    moves.add(BLACK_RIGHT_CASTLE);
                }
                ChessGame.this.gameBoard = trueBoard;
            }

            return moves;
        }

        /**
         * Moves rook if a castle was executed
         *
         * @param move the move that might have been a castle
         */
        public void executeCastle(ChessMove move) {
            if(move.equals(WHITE_LEFT_CASTLE)) {
                ChessGame.this.gameBoard.movePiece(new ChessMove(WHITE_LEFT_ROOK_START, new ChessPosition(1,4), null));
            }
            if(move.equals(WHITE_RIGHT_CASTLE)) {
                ChessGame.this.gameBoard.movePiece(new ChessMove(WHITE_RIGHT_ROOK_START, new ChessPosition(1,6), null));
            }
            if(move.equals(BLACK_LEFT_CASTLE)) {
                ChessGame.this.gameBoard.movePiece(new ChessMove(BLACK_LEFT_ROOK_START, new ChessPosition(8,4), null));
            }
            if(move.equals(BLACK_RIGHT_CASTLE)) {
                ChessGame.this.gameBoard.movePiece(new ChessMove(BLACK_RIGHT_ROOK_START, new ChessPosition(8,6), null));
            }
        }
    }

    /**
     * This class manages the data related to En Passanting.
     */
    private class EnPassanter {
        private ChessMove enPassantLeft = null;
        private ChessMove enPassantRight = null;

        /**
         * Checks if the move made makes an En Passant available.
         * Updates this classes variables to reflect any changes.
         * Checks after the move is made
         *
         * @param move the move that may make an En Passant available
         */
        public void checkEnPassantOpportunities(ChessMove move) {
            ChessPiece piece = ChessGame.this.gameBoard.getPiece(move.getEndPosition());
            if(piece.getPieceType() == ChessPiece.PieceType.PAWN
                    && move.getStartPosition().getColumn() == move.getEndPosition().getColumn()
                    && Math.abs(move.getStartPosition().getRow() - move.getEndPosition().getRow()) >= 2) {

                ChessPosition enPassanterPosition = new ChessPosition(move.getEndPosition().getRow(), move.getEndPosition().getColumn()+1);
                if(ChessPieceMoveFinder.isOnBoard(enPassanterPosition)
                        && ChessGame.this.gameBoard.getPiece(enPassanterPosition) != null
                        && ChessGame.this.gameBoard.getPiece(enPassanterPosition).getPieceType() == ChessPiece.PieceType.PAWN
                        && ChessGame.this.gameBoard.getPieceColor(enPassanterPosition) != piece.getTeamColor()) {

                    ChessPosition enPassanterEndPosition = new ChessPosition(move.getEndPosition().getRow()-1, move.getEndPosition().getColumn());
                    if(move.getEndPosition().getRow() < move.getStartPosition().getRow()) {
                        enPassanterEndPosition = new ChessPosition(move.getEndPosition().getRow()+1, move.getEndPosition().getColumn());
                    }
                    enPassantLeft = new ChessMove(enPassanterPosition, enPassanterEndPosition, null);
                }
                else {
                    enPassantLeft = null;
                }

                enPassanterPosition = new ChessPosition(move.getEndPosition().getRow(), move.getEndPosition().getColumn()-1);
                if(ChessPieceMoveFinder.isOnBoard(enPassanterPosition)
                        && ChessGame.this.gameBoard.getPiece(enPassanterPosition) != null
                        && ChessGame.this.gameBoard.getPiece(enPassanterPosition).getPieceType() == ChessPiece.PieceType.PAWN
                        && ChessGame.this.gameBoard.getPieceColor(enPassanterPosition) != piece.getTeamColor()) {

                    ChessPosition enPassanterEndPosition = new ChessPosition(move.getEndPosition().getRow()-1, move.getEndPosition().getColumn());
                    if(move.getEndPosition().getRow() < move.getStartPosition().getRow()) {
                        enPassanterEndPosition = new ChessPosition(move.getEndPosition().getRow()+1, move.getEndPosition().getColumn());
                    }
                    enPassantRight = new ChessMove(enPassanterPosition, enPassanterEndPosition, null);
                }
                else {
                    enPassantRight = null;
                }

            }
            else {
                enPassantLeft = null;
                enPassantRight = null;
            }
        }

        /**
         * Removes the pawn that was captured by an En Passant
         *
         * @param move the move that was an En Passant
         */
        public void executeEnPassant(ChessMove move) {
            ChessPosition targetPawn = new ChessPosition(move.getStartPosition().getRow(), move.getEndPosition().getColumn());
            ChessGame.this.gameBoard.addPiece(targetPawn, null);
        }

        /**
         * Returns any En Passant moves available from the start position
         *
         * @param startPosition the piece to check if it can En Passant
         * @return Collection of En Passant moves available.
         */
        public Collection<ChessMove> getEnPassantMoves(ChessPosition startPosition) {
            Collection<ChessMove> moves = new HashSet<>();

            if(enPassantRight != null
                    && enPassantRight.getStartPosition().equals(startPosition)) {
                moves.add(enPassantRight);
            }

            if(enPassantLeft != null
                    && enPassantLeft.getStartPosition().equals(startPosition)) {
                moves.add(enPassantLeft);
            }

            return moves;
        }
    }
}
