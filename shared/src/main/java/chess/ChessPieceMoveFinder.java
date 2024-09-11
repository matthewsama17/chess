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
        HashSet<ChessMove> moves = new HashSet<ChessMove>();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int advance = 0;
        boolean atStart = false;
        boolean atEnd = false;

        if(myColor == ChessGame.TeamColor.WHITE) {
            advance = 1;
            if(row == 2) {
                atStart = true;
            }
            if(row == 7) {
                atEnd = true;
            }
        }
        if(myColor == ChessGame.TeamColor.BLACK) {
            advance = -1;
            if(row == 2) {
                atEnd = true;
            }
            if(row == 7) {
                atStart = true;
            }
        }

        ChessPosition target = new ChessPosition(row+advance, col);
        if(board.getPieceColor(target) == null) {
            moves.add(new ChessMove(myPosition, target, null));

            if(atStart) {
                target = new ChessPosition(row+2*advance, col);
                if(board.getPieceColor(target) == null) {
                    moves.add(new ChessMove(myPosition, target, null));
                }
            }
        }

Op        int i = 1;
        while(true) {
            target = new ChessPosition(row+advance, col+i);
            if(isOnBoard(target) && board.getPieceColor(target) != null && board.getPieceColor(target) != myColor) {
                moves.add(new ChessMove(myPosition, target, null));
            }

            if(i == 1) {
                i = -1;
                continue;
            }
            break;

        }

        if(atEnd) {
            return getPromotions(moves);
        }

        return moves;
    }

    private static HashSet<ChessMove> getPromotions(HashSet<ChessMove> moves) {
        HashSet<ChessMove> promotionMoves = new HashSet<ChessMove>();

        for(ChessMove move : moves) {
            ChessPosition startPosition = move.getStartPosition();
            ChessPosition endPosition= move.getEndPosition();
            promotionMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.QUEEN));
            promotionMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.BISHOP));
            promotionMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.KNIGHT));
            promotionMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.ROOK));
        }

        return promotionMoves;
    }

    /**
     * Calculates all the positions a king can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public static HashSet<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {
        HashSet<ChessMove> moves = new HashSet<ChessMove>();

        moves.addAll(jump(board, myPosition, myColor, 1, 0));
        moves.addAll(jump(board, myPosition, myColor, 1, 1));
        moves.addAll(jump(board, myPosition, myColor, 0, 1));
        moves.addAll(jump(board, myPosition, myColor, -1, 1));
        moves.addAll(jump(board, myPosition, myColor, -1, 0));
        moves.addAll(jump(board, myPosition, myColor, -1, -1));
        moves.addAll(jump(board, myPosition, myColor, 0, -1));
        moves.addAll(jump(board, myPosition, myColor, 1, -1));

        return moves;
    }

    /**
     * Calculates all the positions a knight can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public static HashSet<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {
        HashSet<ChessMove> moves = new HashSet<ChessMove>();

        moves.addAll(jump(board, myPosition, myColor, 2, 1));
        moves.addAll(jump(board, myPosition, myColor, 1, 2));
        moves.addAll(jump(board, myPosition, myColor, -1, 2));
        moves.addAll(jump(board, myPosition, myColor, -2, 1));
        moves.addAll(jump(board, myPosition, myColor, -2, -1));
        moves.addAll(jump(board, myPosition, myColor, -1, -2));
        moves.addAll(jump(board, myPosition, myColor, 1, -2));
        moves.addAll(jump(board, myPosition, myColor, 2, -1));

        return moves;
    }

    /**
     * Calculates all the positions a rook can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public static HashSet<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {
        HashSet<ChessMove> moves = new HashSet<ChessMove>();

        moves.addAll(slide(board, myPosition, myColor, 1, 0));
        moves.addAll(slide(board, myPosition, myColor, 0, 1));
        moves.addAll(slide(board, myPosition, myColor, -1, 0));
        moves.addAll(slide(board, myPosition, myColor, 0, -1));

        return moves;
    }

    /**
     * Calculates all the positions a bishop can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public static HashSet<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {
        HashSet<ChessMove> moves = new HashSet<ChessMove>();

        moves.addAll(slide(board, myPosition, myColor, 1, 1));
        moves.addAll(slide(board, myPosition, myColor, -1, 1));
        moves.addAll(slide(board, myPosition, myColor, -1, -1));
        moves.addAll(slide(board, myPosition, myColor, 1, -1));

        return moves;
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

    private static HashSet<ChessMove> jump(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor, int rowOffset, int columnOffset) {
        HashSet<ChessMove> moves = new HashSet<ChessMove>();
        ChessPosition target = new ChessPosition(myPosition.getRow()+rowOffset, myPosition.getColumn()+columnOffset);

        if(!isOnBoard(target)) {
            return moves;
        }
        ChessGame.TeamColor targetColor = board.getPieceColor(target);
        if(targetColor == myColor) {
            return moves;
        }
        moves.add(new ChessMove(myPosition, target, null));
        return moves;
    }

    private static HashSet<ChessMove> slide(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor, int rowOffset, int columnOffset) {
        HashSet<ChessMove> moves = new HashSet<ChessMove>();

        int row = myPosition.getRow() + rowOffset;
        int col = myPosition.getColumn() + columnOffset;
        ChessPosition target = new ChessPosition(row, col);
        while(isOnBoard(target)) {
            ChessGame.TeamColor targetColor = board.getPieceColor(target);

            if(targetColor == myColor) {
                break;
            }
            moves.add(new ChessMove(myPosition, target, null));
            if(targetColor != null) {
                break;
            }

            //increment
            row += rowOffset;
            col += columnOffset;
            target = new ChessPosition(row, col);
        }

        return moves;
    }
}