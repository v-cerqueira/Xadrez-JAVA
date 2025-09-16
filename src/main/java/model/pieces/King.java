package model.pieces;

import model.Board;
import model.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa um rei no jogo de xadrez
 */
public class King extends Piece {
    
    public King(boolean isWhite, Position position) {
        super(isWhite, position);
    }
    
    @Override
    public List<Position> getPossibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        
        // Movimentos em todas as direções (uma casa)
        int[][] directions = {
            {0, 1}, {0, -1}, {1, 0}, {-1, 0},  // Horizontal e vertical
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1} // Diagonal
        };
        
        for (int[] direction : directions) {
            int newRow = position.getRow() + direction[0];
            int newCol = position.getCol() + direction[1];
            Position target = new Position(newRow, newCol);
            
            if (isValidPosition(target) && !hasAllyPieceAt(board, target)) {
                moves.add(target);
            }
        }
        
        // Roque (castling)
        if (!hasMoved) {
            addCastlingMoves(moves, board);
        }
        
        return moves;
    }
    
    /**
     * Adiciona movimentos de roque se válidos
     */
    private void addCastlingMoves(List<Position> moves, Board board) {
        // Roque do lado do rei (roque curto)
        Position kingSideRook = new Position(position.getRow(), 7);
        if (canCastle(kingSideRook, board)) {
            moves.add(new Position(position.getRow(), 6));
        }
        
        // Roque do lado da rainha (roque longo)
        Position queenSideRook = new Position(position.getRow(), 0);
        if (canCastle(queenSideRook, board)) {
            moves.add(new Position(position.getRow(), 2));
        }
    }
    
    /**
     * Verifica se o roque é possível com a torre especificada
     */
    private boolean canCastle(Position rookPosition, Board board) {
        Piece rook = board.getPieceAt(rookPosition);
        if (!(rook instanceof Rook) || rook.hasMoved() || rook.isWhite() != isWhite) {
            return false;
        }
        
        // Verifica se o caminho está livre
        int direction = rookPosition.getCol() > position.getCol() ? 1 : -1;
        int currentCol = position.getCol() + direction;
        
        while (currentCol != rookPosition.getCol()) {
            Position current = new Position(position.getRow(), currentCol);
            if (!board.isEmpty(current)) {
                return false;
            }
            currentCol += direction;
        }
        // Regras adicionais de roque real:
        // - O rei não pode estar em xeque
        // - As casas pelas quais o rei passa (coluna +/-1) e a casa de destino não podem estar atacadas
        boolean enemyIsWhite = !isWhite;
        Position from = position;
        Position through = new Position(position.getRow(), position.getCol() + direction);
        Position to = new Position(position.getRow(), position.getCol() + 2 * direction);
        if (board.isSquareAttacked(from, enemyIsWhite)) {
            return false;
        }
        if (board.isSquareAttacked(through, enemyIsWhite)) {
            return false;
        }
        if (board.isSquareAttacked(to, enemyIsWhite)) {
            return false;
        }
        return true;
    }
    
    @Override
    public boolean isValidMove(Position to, Board board) {
        if (!isValidPosition(to)) {
            return false;
        }
        
        int rowDiff = Math.abs(to.getRow() - position.getRow());
        int colDiff = Math.abs(to.getCol() - position.getCol());
        
        // Movimento normal do rei (uma casa em qualquer direção)
        if (rowDiff <= 1 && colDiff <= 1) {
            return !hasAllyPieceAt(board, to);
        }
        
        // Roque
        if (rowDiff == 0 && colDiff == 2 && !hasMoved) {
            return isValidCastling(to, board);
        }
        
        return false;
    }
    
    /**
     * Verifica se o roque é válido
     */
    private boolean isValidCastling(Position to, Board board) {
        int direction = to.getCol() > position.getCol() ? 1 : -1;
        Position rookPosition = new Position(position.getRow(), direction > 0 ? 7 : 0);
        
        return canCastle(rookPosition, board);
    }
    
    @Override
    public String getSymbol() {
        return isWhite ? "K" : "k";
    }
    
    @Override
    public String getName() {
        return "Rei";
    }
}
