package model.pieces;

import model.Board;
import model.Position;
import java.util.List;

/**
 * Classe abstrata que representa uma peça de xadrez
 */
public abstract class Piece {
    protected boolean isWhite;
    protected Position position;
    protected boolean hasMoved;
    
    public Piece(boolean isWhite, Position position) {
        this.isWhite = isWhite;
        this.position = position;
        this.hasMoved = false;
    }
    
    public boolean isWhite() {
        return isWhite;
    }
    
    public Position getPosition() {
        return position;
    }
    
    public void setPosition(Position position) {
        this.position = position;
    }
    
    public boolean hasMoved() {
        return hasMoved;
    }
    
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
    
    /**
     * Retorna todos os movimentos possíveis para esta peça
     */
    public abstract List<Position> getPossibleMoves(Board board);
    
    /**
     * Verifica se um movimento é válido para esta peça
     */
    public abstract boolean isValidMove(Position to, Board board);
    
    /**
     * Retorna o símbolo da peça para notação de xadrez
     */
    public abstract String getSymbol();
    
    /**
     * Retorna o nome da peça
     */
    public abstract String getName();
    
    /**
     * Verifica se a posição está dentro dos limites do tabuleiro
     */
    protected boolean isValidPosition(Position pos) {
        return pos != null && pos.isValid();
    }
    
    /**
     * Verifica se há uma peça na posição especificada
     */
    protected boolean hasPieceAt(Board board, Position pos) {
        return board.getPieceAt(pos) != null;
    }
    
    /**
     * Verifica se há uma peça inimiga na posição especificada
     */
    protected boolean hasEnemyPieceAt(Board board, Position pos) {
        Piece piece = board.getPieceAt(pos);
        return piece != null && piece.isWhite() != this.isWhite;
    }
    
    /**
     * Verifica se há uma peça aliada na posição especificada
     */
    protected boolean hasAllyPieceAt(Board board, Position pos) {
        Piece piece = board.getPieceAt(pos);
        return piece != null && piece.isWhite() == this.isWhite;
    }
    
    @Override
    public String toString() {
        return (isWhite ? "Branco" : "Preto") + " " + getName();
    }
}
