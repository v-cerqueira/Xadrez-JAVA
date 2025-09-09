package model;

import model.pieces.*;

/**
 * Representa o tabuleiro de xadrez
 */
public class Board {
    private Piece[][] squares;
    
    public Board() {
        squares = new Piece[8][8];
        initializeBoard();
    }
    
    /**
     * Inicializa o tabuleiro com as peças na posição inicial
     */
    private void initializeBoard() {
        // Peças pretas
        squares[0][0] = new Rook(false, new Position(0, 0));
        squares[0][1] = new Knight(false, new Position(0, 1));
        squares[0][2] = new Bishop(false, new Position(0, 2));
        squares[0][3] = new Queen(false, new Position(0, 3));
        squares[0][4] = new King(false, new Position(0, 4));
        squares[0][5] = new Bishop(false, new Position(0, 5));
        squares[0][6] = new Knight(false, new Position(0, 6));
        squares[0][7] = new Rook(false, new Position(0, 7));
        
        for (int col = 0; col < 8; col++) {
            squares[1][col] = new Pawn(false, new Position(1, col));
        }
        
        // Peças brancas
        for (int col = 0; col < 8; col++) {
            squares[6][col] = new Pawn(true, new Position(6, col));
        }
        
        squares[7][0] = new Rook(true, new Position(7, 0));
        squares[7][1] = new Knight(true, new Position(7, 1));
        squares[7][2] = new Bishop(true, new Position(7, 2));
        squares[7][3] = new Queen(true, new Position(7, 3));
        squares[7][4] = new King(true, new Position(7, 4));
        squares[7][5] = new Bishop(true, new Position(7, 5));
        squares[7][6] = new Knight(true, new Position(7, 6));
        squares[7][7] = new Rook(true, new Position(7, 7));
    }
    
    /**
     * Retorna a peça na posição especificada
     */
    public Piece getPieceAt(Position position) {
        if (!position.isValid()) {
            return null;
        }
        return squares[position.getRow()][position.getCol()];
    }
    
    /**
     * Coloca uma peça na posição especificada
     */
    public void setPieceAt(Position position, Piece piece) {
        if (position.isValid()) {
            squares[position.getRow()][position.getCol()] = piece;
            if (piece != null) {
                piece.setPosition(position);
            }
        }
    }
    
    /**
     * Remove a peça da posição especificada
     */
    public void removePieceAt(Position position) {
        if (position.isValid()) {
            squares[position.getRow()][position.getCol()] = null;
        }
    }
    
    /**
     * Move uma peça de uma posição para outra
     */
    public void movePiece(Position from, Position to) {
        Piece piece = getPieceAt(from);
        if (piece != null) {
            removePieceAt(from);
            setPieceAt(to, piece);
            piece.setHasMoved(true);
        }
    }
    
    /**
     * Verifica se uma posição está vazia
     */
    public boolean isEmpty(Position position) {
        return getPieceAt(position) == null;
    }
    
    /**
     * Retorna uma cópia do tabuleiro
     */
    public Board copy() {
        Board copy = new Board();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                copy.squares[row][col] = null;
            }
        }
        
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = squares[row][col];
                if (piece != null) {
                    copy.squares[row][col] = createPieceCopy(piece, new Position(row, col));
                }
            }
        }
        
        return copy;
    }
    
    /**
     * Cria uma cópia de uma peça
     */
    private Piece createPieceCopy(Piece original, Position newPosition) {
        if (original instanceof Pawn) {
            return new Pawn(original.isWhite(), newPosition);
        } else if (original instanceof Rook) {
            return new Rook(original.isWhite(), newPosition);
        } else if (original instanceof Knight) {
            return new Knight(original.isWhite(), newPosition);
        } else if (original instanceof Bishop) {
            return new Bishop(original.isWhite(), newPosition);
        } else if (original instanceof Queen) {
            return new Queen(original.isWhite(), newPosition);
        } else if (original instanceof King) {
            return new King(original.isWhite(), newPosition);
        }
        return null;
    }
    
    /**
     * Encontra a posição do rei de uma cor específica
     */
    public Position findKing(boolean isWhite) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = squares[row][col];
                if (piece instanceof King && piece.isWhite() == isWhite) {
                    return new Position(row, col);
                }
            }
        }
        return null;
    }
}
