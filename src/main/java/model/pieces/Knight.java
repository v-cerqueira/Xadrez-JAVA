package model.pieces;

import model.Board;
import model.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa um cavalo no jogo de xadrez
 */
public class Knight extends Piece {
    
    public Knight(boolean isWhite, Position position) {
        super(isWhite, position);
    }
    
    @Override
    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        
        // Movimentos em L do cavalo
        int[][] knightMoves = {
            {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
            {1, -2}, {1, 2}, {2, -1}, {2, 1}
        };
        
        for (int[] move : knightMoves) {
            int newRow = position.getRow() + move[0];
            int newCol = position.getCol() + move[1];
            Position target = new Position(newRow, newCol);
            
            if (isValidPosition(target) && !hasAllyPieceAt(board, target)) {
                moves.add(target);
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
        
        // Cavalo move em L: 2 casas em uma direção e 1 na perpendicular
        boolean isValidKnightMove = (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
        
        if (!isValidKnightMove) {
            return false;
        }
        
        // Verifica se não há peça aliada na posição de destino
        return !hasAllyPieceAt(board, to);
    }
    
    @Override
    public String getSymbol() {
        return isWhite ? "N" : "n";
    }
    
    @Override
    public String getName() {
        return "Cavalo";
    }
}
