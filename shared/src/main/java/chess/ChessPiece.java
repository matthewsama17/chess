package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor color;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.color = pieceColor;
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
    public ChessGame.TeamColor getTeamColor() { return color; }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() { return type; }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if(type == PieceType.PAWN) {
            return pawnMoves(board,myPosition);
        }
        else if(type == PieceType.ROOK) {
            return rookMoves(board,myPosition);
        }
        else if(type == PieceType.KNIGHT) {
            return knightMoves(board,myPosition);
        }
        else if(type == PieceType.BISHOP) {
            return bishopMoves(board,myPosition);
        }
        else if(type == PieceType.QUEEN) {
            return queenMoves(board,myPosition);
        }
        else if(type == PieceType.KING) {
            return kingMoves(board,myPosition);
        }
        return null;
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented");
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        int r = myPosition.getRow();
        int c = myPosition.getColumn();
        ChessPosition endPosition = null;
        Collection<ChessMove> moves = new HashSet<ChessMove>();

        //up
        for(int i = 1; (r+i) <= 8; i++) {
            endPosition = new ChessPosition(r+i,c);
            ChessGame.TeamColor otherColor = board.getPieceColor(endPosition);
            if(otherColor == color) {
                break;
            }
            else if(otherColor == null) {
                moves.add(new ChessMove(myPosition,endPosition,null));
            }
            else {
                moves.add(new ChessMove(myPosition,endPosition,null));
                break;
            }
        }

        //down
        for(int i = 1; (r-i) >= 1; i++) {
            endPosition = new ChessPosition(r-i,c);
            ChessGame.TeamColor otherColor = board.getPieceColor(endPosition);
            if(otherColor == color) {
                break;
            }
            else if(otherColor == null) {
                moves.add(new ChessMove(myPosition,endPosition,null));
            }
            else {
                moves.add(new ChessMove(myPosition,endPosition,null));
                break;
            }
        }

        //right
        for(int i = 1; (c+i) <= 8; i++) {
            endPosition = new ChessPosition(r,c+i);
            ChessGame.TeamColor otherColor = board.getPieceColor(endPosition);
            if(otherColor == color) {
                break;
            }
            else if(otherColor == null) {
                moves.add(new ChessMove(myPosition,endPosition,null));
            }
            else {
                moves.add(new ChessMove(myPosition,endPosition,null));
                break;
            }
        }

        //left
        for(int i = 1; (c-i) >= 1; i++) {
            endPosition = new ChessPosition(r,c-i);
            ChessGame.TeamColor otherColor = board.getPieceColor(endPosition);
            if(otherColor == color) {
                break;
            }
            else if(otherColor == null) {
                moves.add(new ChessMove(myPosition,endPosition,null));
            }
            else {
                moves.add(new ChessMove(myPosition,endPosition,null));
                break;
            }
        }

        return moves;
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        int r = myPosition.getRow();
        int c = myPosition.getColumn();
        ChessPosition endPosition = null;
        Collection<ChessMove> moves = new HashSet<ChessMove>();

        //up right
        if(c < 8 && r < 7) {
            endPosition = new ChessPosition(r+2,c+1);
            if(board.getPieceColor(endPosition) != color) {
                moves.add(new ChessMove(myPosition, endPosition, null));
            }
        }

        //right up
        if(c < 7 && r < 8) {
            endPosition = new ChessPosition(r+1,c+2);
            if(board.getPieceColor(endPosition) != color) {
                moves.add(new ChessMove(myPosition, endPosition, null));
            }
        }

        //right down
        if(c < 7 && r > 1) {
            endPosition = new ChessPosition(r-1,c+2);
            if(board.getPieceColor(endPosition) != color) {
                moves.add(new ChessMove(myPosition, endPosition, null));
            }
        }

        //down right
        if(c < 8 && r > 2) {
            endPosition = new ChessPosition(r-2,c+1);
            if(board.getPieceColor(endPosition) != color) {
                moves.add(new ChessMove(myPosition, endPosition, null));
            }
        }

        //down left
        if(c > 1 && r > 2) {
            endPosition = new ChessPosition(r-2,c-1);
            if(board.getPieceColor(endPosition) != color) {
                moves.add(new ChessMove(myPosition, endPosition, null));
            }
        }

        //left down
        if(c > 2 && r > 1) {
            endPosition = new ChessPosition(r-1,c-2);
            if(board.getPieceColor(endPosition) != color) {
                moves.add(new ChessMove(myPosition, endPosition, null));
            }
        }

        //left up
        if(c > 2 && r < 8) {
            endPosition = new ChessPosition(r+1,c-2);
            if(board.getPieceColor(endPosition) != color) {
                moves.add(new ChessMove(myPosition, endPosition, null));
            }
        }

        //up left
        if(c > 1 && r < 7) {
            endPosition = new ChessPosition(r+2,c-1);
            if(board.getPieceColor(endPosition) != color) {
                moves.add(new ChessMove(myPosition, endPosition, null));
            }
        }

        return moves;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        int r = myPosition.getRow();
        int c = myPosition.getColumn();
        ChessPosition endPosition = null;
        Collection<ChessMove> moves = new HashSet<ChessMove>();

        //up right
        for(int i = 1; (r+i) <= 8 && (c+i) <= 8; i++) {
            endPosition = new ChessPosition(r+i,c+i);
            ChessGame.TeamColor otherColor = board.getPieceColor(endPosition);
            if(otherColor == color) {
                break;
            }
            else if(otherColor == null) {
                moves.add(new ChessMove(myPosition,endPosition,null));
            }
            else {
                moves.add(new ChessMove(myPosition,endPosition,null));
                break;
            }
        }

        //up left
        for(int i = 1; (r+i) <= 8 && (c-i) >= 1; i++) {
            endPosition = new ChessPosition(r+i,c-i);
            ChessGame.TeamColor otherColor = board.getPieceColor(endPosition);
            if(otherColor == color) {
                break;
            }
            else if(otherColor == null) {
                moves.add(new ChessMove(myPosition,endPosition,null));
            }
            else {
                moves.add(new ChessMove(myPosition,endPosition,null));
                break;
            }
        }

        //down left
        for(int i = 1; (r-i) >= 1 && (c-i) >= 1; i++) {
            endPosition = new ChessPosition(r-i,c-i);
            ChessGame.TeamColor otherColor = board.getPieceColor(endPosition);
            if(otherColor == color) {
                break;
            }
            else if(otherColor == null) {
                moves.add(new ChessMove(myPosition,endPosition,null));
            }
            else {
                moves.add(new ChessMove(myPosition,endPosition,null));
                break;
            }
        }

        //down right
        for(int i = 1; (r-i) >= 1 && (c+i) <= 8; i++) {
            endPosition = new ChessPosition(r-i,c+i);
            ChessGame.TeamColor otherColor = board.getPieceColor(endPosition);
            if(otherColor == color) {
                break;
            }
            else if(otherColor == null) {
                moves.add(new ChessMove(myPosition,endPosition,null));
            }
            else {
                moves.add(new ChessMove(myPosition,endPosition,null));
                break;
            }
        }

        return moves;
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = bishopMoves(board,myPosition);
        moves.addAll(rookMoves(board,myPosition));
        return moves;
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        int r = myPosition.getRow();
        int c = myPosition.getColumn();
        ChessPosition endPosition = null;
        Collection<ChessMove> moves = new HashSet<ChessMove>();

        //right
        if(c < 8) {
            endPosition = new ChessPosition(r, c + 1);
            if(board.getPieceColor(endPosition) != color) {
                moves.add(new ChessMove(myPosition, endPosition, null));
            }
        }

        //left
        if(c > 1) {
            endPosition = new ChessPosition(r, c - 1);
            if(board.getPieceColor(endPosition) != color) {
                moves.add(new ChessMove(myPosition, endPosition, null));
            }
        }

        //up
        if(r < 8) {
            endPosition = new ChessPosition(r + 1, c);
            if(board.getPieceColor(endPosition) != color) {
                moves.add(new ChessMove(myPosition, endPosition, null));
            }
        }

        //down
        if(r > 1) {
            endPosition = new ChessPosition(r - 1, c);
            if(board.getPieceColor(endPosition) != color) {
                moves.add(new ChessMove(myPosition, endPosition, null));
            }
        }

        //up right
        if(r < 8 && c < 8) {
            endPosition = new ChessPosition(r + 1, c + 1);
            if(board.getPieceColor(endPosition) != color) {
                moves.add(new ChessMove(myPosition, endPosition, null));
            }
        }

        //down left
        if(r > 1 && c > 1) {
            endPosition = new ChessPosition(r - 1, c - 1);
            if(board.getPieceColor(endPosition) != color) {
                moves.add(new ChessMove(myPosition, endPosition, null));
            }
        }

        //up left
        if(r < 8 && c > 1) {
            endPosition = new ChessPosition(r + 1, c - 1);
            if(board.getPieceColor(endPosition) != color) {
                moves.add(new ChessMove(myPosition, endPosition, null));
            }
        }

        //down right
        if(r > 1 && c < 8) {
            endPosition = new ChessPosition(r - 1, c + 1);
            if(board.getPieceColor(endPosition) != color) {
                moves.add(new ChessMove(myPosition, endPosition, null));
            }
        }

        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return color == that.color && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }
}
