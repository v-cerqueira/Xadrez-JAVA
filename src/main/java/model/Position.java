package model;

/**
 * Representa uma posição no tabuleiro de xadrez
 */
public class Position {
    private int row;
    private int col;
    
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }
    
    public int getRow() {
        return row;
    }
    
    public int getCol() {
        return col;
    }
    
    public void setRow(int row) {
        this.row = row;
    }
    
    public void setCol(int col) {
        this.col = col;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return row == position.row && col == position.col;
    }
    
    @Override
    public int hashCode() {
        return row * 8 + col;
    }
    
    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }
    
    /**
     * Verifica se a posição está dentro dos limites do tabuleiro
     */
    public boolean isValid() {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
}
