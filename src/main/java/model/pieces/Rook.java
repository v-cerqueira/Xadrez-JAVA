package model.pieces;

import model.Board;
import model.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa uma torre no jogo de xadrez
 */
public class Rook extends Piece {
    
    public Rook(boolean isWhite, Position position) {
        super(isWhite, position);
    }
    
    @Override
    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        
        // Movimentos horizontais e verticais
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        
        for (int[] direction : directions) {
            int row = position.getRow() + direction[0];
            int col = position.getCol() + direction[1];
            
            while (row >= 0 && row < 8 && col >= 0 && col < 8) {
                Position target = new Position(row, col);
                
                if (board.isEmpty(target)) {
                    moves.add(target);
                } else if (hasEnemyPieceAt(board, target)) {
                    moves.add(target);
                    break;
                } else {
                    break; // Peça aliada bloqueando
                }
                
                row += direction[0];
                col += direction[1];
            }
        }
        
        return moves;
    }
    
    @Override
    public boolean isValidMove(Position to, Board board) {
        if (!isValidPosition(to)) {
            return false;
        }
        
        int rowDiff = Math.abs(to.getRow() - position.getRow());
        int colDiff = Math.abs(to.getCol() - position.getCol());
        
        // Torre move apenas em linha reta (horizontal ou vertical)
        if (rowDiff != 0 && colDiff != 0) {
            return false;
        }
        
        // Verifica se o caminho está livre
        return isPathClear(position, to, board);
    }
    
    /**
     * Verifica se o caminho entre duas posições está livre
     */
    private boolean isPathClear(Position from, Position to, Board board) {
        int rowStep = Integer.compare(to.getRow(), from.getRow());
        int colStep = Integer.compare(to.getCol(), from.getCol());
        
        int currentRow = from.getRow() + rowStep;
        int currentCol = from.getCol() + colStep;
        
        while (currentRow != to.getRow() || currentCol != to.getCol()) {
            Position current = new Position(currentRow, currentCol);
            if (!board.isEmpty(current)) {
                return false;
            }
            currentRow += rowStep;
            currentCol += colStep;
        }
        
        // Verifica se a posição de destino está vazia ou tem peça inimiga
        return board.isEmpty(to) || hasEnemyPieceAt(board, to);
    }
    
    @Override
    public String getSymbol() {
        return isWhite ? "R" : "r";
    }
    
    @Override
    public String getName() {
        return "Torre";
    }
}
