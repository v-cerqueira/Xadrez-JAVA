package model.pieces;

import model.Board;
import model.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa um peão no jogo de xadrez
 */
public class Pawn extends Piece {
    
    public Pawn(boolean isWhite, Position position) {
        super(isWhite, position);
    }
    
    @Override
    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        int direction = isWhite ? -1 : 1; // Peões brancos sobem (linha diminui), pretos descem (linha aumenta)
        int startRow = isWhite ? 6 : 1; // Linha inicial dos peões
        
        // Movimento para frente
        Position oneForward = new Position(position.getRow() + direction, position.getCol());
        if (isValidPosition(oneForward) && board.isEmpty(oneForward)) {
            moves.add(oneForward);
            
            // Movimento duplo do peão (apenas na posição inicial)
            if (position.getRow() == startRow) {
                Position twoForward = new Position(position.getRow() + 2 * direction, position.getCol());
                if (isValidPosition(twoForward) && board.isEmpty(twoForward)) {
                    moves.add(twoForward);
                }
            }
        }
        
        // Capturas diagonais
        Position leftDiagonal = new Position(position.getRow() + direction, position.getCol() - 1);
        if (isValidPosition(leftDiagonal) && hasEnemyPieceAt(board, leftDiagonal)) {
            moves.add(leftDiagonal);
        }
        
        Position rightDiagonal = new Position(position.getRow() + direction, position.getCol() + 1);
        if (isValidPosition(rightDiagonal) && hasEnemyPieceAt(board, rightDiagonal)) {
            moves.add(rightDiagonal);
        }

        // En passant
        Position enPassant = board.getEnPassantTarget();
        if (enPassant != null && enPassant.getRow() == position.getRow() + direction) {
            if (Math.abs(enPassant.getCol() - position.getCol()) == 1) {
                moves.add(new Position(enPassant.getRow(), enPassant.getCol()));
            }
        }
        
        return moves;
    }
    
    @Override
    public boolean isValidMove(Position to, Board board) {
        if (!isValidPosition(to)) {
            return false;
        }
        
        int direction = isWhite ? -1 : 1;
        int startRow = isWhite ? 6 : 1;
        int rowDiff = to.getRow() - position.getRow();
        int colDiff = Math.abs(to.getCol() - position.getCol());
        
        // Movimento para frente
        if (colDiff == 0) {
            if (rowDiff == direction && board.isEmpty(to)) {
                return true;
            }
            // Movimento duplo
            if (rowDiff == 2 * direction && position.getRow() == startRow && board.isEmpty(to)) {
                Position intermediate = new Position(position.getRow() + direction, position.getCol());
                return board.isEmpty(intermediate);
            }
        }
        // Captura diagonal
        else if (colDiff == 1 && rowDiff == direction) {
            return hasEnemyPieceAt(board, to);
        }
        // En passant: destino coincide com alvo e a casa final está vazia
        Position ep = board.getEnPassantTarget();
        if (ep != null && ep.equals(to) && colDiff == 1 && rowDiff == direction) {
            return board.isEmpty(to);
        }
        
        return false;
    }
    
    @Override
    public String getSymbol() {
        return isWhite ? "P" : "p";
    }
    
    @Override
    public String getName() {
        return "Peão";
    }
}
