package model;

import model.pieces.Piece;

/**
 * Representa um movimento no jogo de xadrez
 */
public class Move {
    private Position from;
    private Position to;
    private Piece pieceMoved;
    private Piece pieceCaptured;
    private boolean isCastling;
    private boolean isEnPassant;
    private Piece promotionPiece;
    
    public Move(Position from, Position to, Piece pieceMoved) {
        this.from = from;
        this.to = to;
        this.pieceMoved = pieceMoved;
        this.isCastling = false;
        this.isEnPassant = false;
        this.promotionPiece = null;
    }
    
    public Move(Position from, Position to, Piece pieceMoved, Piece pieceCaptured) {
        this(from, to, pieceMoved);
        this.pieceCaptured = pieceCaptured;
    }
    
    public Position getFrom() {
        return from;
    }
    
    public Position getTo() {
        return to;
    }
    
    public Piece getPieceMoved() {
        return pieceMoved;
    }
    
    public Piece getPieceCaptured() {
        return pieceCaptured;
    }
    
    public void setPieceCaptured(Piece pieceCaptured) {
        this.pieceCaptured = pieceCaptured;
    }
    
    public boolean isCastling() {
        return isCastling;
    }
    
    public void setCastling(boolean castling) {
        isCastling = castling;
    }
    
    public boolean isEnPassant() {
        return isEnPassant;
    }
    
    public void setEnPassant(boolean enPassant) {
        isEnPassant = enPassant;
    }
    
    public Piece getPromotionPiece() {
        return promotionPiece;
    }
    
    public void setPromotionPiece(Piece promotionPiece) {
        this.promotionPiece = promotionPiece;
    }
    
    @Override
    public String toString() {
        return pieceMoved + " de " + from + " para " + to;
    }
}
