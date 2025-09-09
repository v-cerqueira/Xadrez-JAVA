package model;

import model.pieces.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Controla o fluxo do jogo de xadrez
 */
public class Game {
    private Board board;
    private boolean isWhiteTurn;
    private List<Move> moveHistory;
    private Position selectedPosition;
    private boolean gameOver;
    private String gameResult;
    private ChessAI ai;
    private boolean aiEnabled;
    
    public Game() {
        this.board = new Board();
        this.isWhiteTurn = true;
        this.moveHistory = new ArrayList<>();
        this.selectedPosition = null;
        this.gameOver = false;
        this.gameResult = null;
        this.ai = new ChessAI();
        this.aiEnabled = false;
    }
    
    public Board getBoard() {
        return board;
    }
    
    public boolean isWhiteTurn() {
        return isWhiteTurn;
    }
    
    public List<Move> getMoveHistory() {
        return new ArrayList<>(moveHistory);
    }
    
    public Position getSelectedPosition() {
        return selectedPosition;
    }
    
    public void setSelectedPosition(Position position) {
        this.selectedPosition = position;
    }
    
    public boolean isGameOver() {
        return gameOver;
    }
    
    public String getGameResult() {
        return gameResult;
    }
    
    /**
     * Seleciona uma peça na posição especificada
     */
    public boolean selectPiece(Position position) {
        Piece piece = board.getPieceAt(position);
        
        // Se não há peça na posição, deseleciona
        if (piece == null) {
            selectedPosition = null;
            return false;
        }
        
        // Se a peça não é do jogador atual, não seleciona
        if (piece.isWhite() != isWhiteTurn) {
            selectedPosition = null;
            return false;
        }
        
        selectedPosition = position;
        return true;
    }
    
    /**
     * Move uma peça para a posição especificada
     */
    public boolean makeMove(Position to) {
        if (selectedPosition == null || gameOver) {
            return false;
        }
        
        Piece piece = board.getPieceAt(selectedPosition);
        if (piece == null) {
            return false;
        }
        
        // Verifica se o movimento é válido
        if (!piece.isValidMove(to, board)) {
            return false;
        }
        
        // Cria o movimento
        Move move = new Move(selectedPosition, to, piece);
        Piece capturedPiece = board.getPieceAt(to);
        if (capturedPiece != null) {
            move.setPieceCaptured(capturedPiece);
        }
        
        // Verifica se o movimento coloca o próprio rei em xeque
        if (wouldPutKingInCheck(move)) {
            return false;
        }
        
        // Executa o movimento
        executeMove(move);
        
        // Adiciona ao histórico
        moveHistory.add(move);
        
        // Verifica fim do jogo
        checkGameEnd();
        
        // Troca o turno
        isWhiteTurn = !isWhiteTurn;
        selectedPosition = null;
        
        // Se a IA está habilitada e é o turno dela, faz o movimento
        if (aiEnabled && ai.shouldMakeMove(this)) {
            makeAIMove();
        }
        
        return true;
    }
    
    /**
     * Executa um movimento no tabuleiro
     */
    private void executeMove(Move move) {
        board.movePiece(move.getFrom(), move.getTo());
        
        // Trata movimentos especiais
        handleSpecialMoves(move);
    }
    
    /**
     * Trata movimentos especiais como roque, promoção, etc.
     */
    private void handleSpecialMoves(Move move) {
        Piece piece = move.getPieceMoved();
        
        // Roque
        if (piece instanceof King && Math.abs(move.getTo().getCol() - move.getFrom().getCol()) == 2) {
            handleCastling(move);
        }
        
        // Promoção de peão
        if (piece instanceof Pawn) {
            int promotionRow = piece.isWhite() ? 0 : 7;
            if (move.getTo().getRow() == promotionRow) {
                handlePawnPromotion(move);
            }
        }
    }
    
    /**
     * Trata o movimento de roque
     */
    private void handleCastling(Move move) {
        int direction = move.getTo().getCol() > move.getFrom().getCol() ? 1 : -1;
        Position rookFrom = new Position(move.getFrom().getRow(), direction > 0 ? 7 : 0);
        Position rookTo = new Position(move.getFrom().getRow(), move.getFrom().getCol() + direction);
        
        board.movePiece(rookFrom, rookTo);
        move.setCastling(true);
    }
    
    /**
     * Trata a promoção de peão (por padrão promove para rainha)
     */
    private void handlePawnPromotion(Move move) {
        Piece newQueen = new Queen(move.getPieceMoved().isWhite(), move.getTo());
        board.setPieceAt(move.getTo(), newQueen);
        move.setPromotionPiece(newQueen);
    }
    
    /**
     * Verifica se um movimento colocaria o próprio rei em xeque
     */
    private boolean wouldPutKingInCheck(Move move) {
        // Cria uma cópia do tabuleiro para simular o movimento
        Board tempBoard = board.copy();
        tempBoard.movePiece(move.getFrom(), move.getTo());
        
        // Verifica se o rei está em xeque após o movimento
        return isKingInCheck(tempBoard, move.getPieceMoved().isWhite());
    }
    
    /**
     * Verifica se o rei de uma cor está em xeque
     */
    public boolean isKingInCheck(Board board, boolean isWhite) {
        Position kingPosition = board.findKing(isWhite);
        if (kingPosition == null) {
            return false;
        }
        
        // Verifica se alguma peça inimiga pode atacar o rei
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPieceAt(new Position(row, col));
                if (piece != null && piece.isWhite() != isWhite) {
                    if (piece.isValidMove(kingPosition, board)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * Verifica se o rei está em xeque no tabuleiro atual
     */
    public boolean isKingInCheck(boolean isWhite) {
        return isKingInCheck(board, isWhite);
    }
    
    /**
     * Verifica se há xeque-mate
     */
    public boolean isCheckmate(boolean isWhite) {
        if (!isKingInCheck(isWhite)) {
            return false;
        }
        
        // Verifica se há algum movimento legal disponível
        return !hasLegalMoves(isWhite);
    }
    
    /**
     * Verifica se há empate (stale mate)
     */
    public boolean isStalemate(boolean isWhite) {
        if (isKingInCheck(isWhite)) {
            return false;
        }
        
        return !hasLegalMoves(isWhite);
    }
    
    /**
     * Verifica se há movimentos legais disponíveis para uma cor
     */
    private boolean hasLegalMoves(boolean isWhite) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPieceAt(new Position(row, col));
                if (piece != null && piece.isWhite() == isWhite) {
                    List<Position> possibleMoves = piece.getPossibleMoves(board);
                    for (Position move : possibleMoves) {
                        Move testMove = new Move(piece.getPosition(), move, piece);
                        if (!wouldPutKingInCheck(testMove)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Verifica o fim do jogo
     */
    private void checkGameEnd() {
        if (isCheckmate(isWhiteTurn)) {
            gameOver = true;
            gameResult = isWhiteTurn ? "Xeque-mate! Pretas ganharam!" : "Xeque-mate! Brancas ganharam!";
        } else if (isStalemate(isWhiteTurn)) {
            gameOver = true;
            gameResult = "Empate por afogamento!";
        }
    }
    
    /**
     * Reinicia o jogo
     */
    public void resetGame() {
        this.board = new Board();
        this.isWhiteTurn = true;
        this.moveHistory.clear();
        this.selectedPosition = null;
        this.gameOver = false;
        this.gameResult = null;
    }
    
    /**
     * Obtém os movimentos possíveis para a peça selecionada
     */
    public List<Position> getPossibleMovesForSelected() {
        if (selectedPosition == null) {
            return new ArrayList<>();
        }
        
        Piece piece = board.getPieceAt(selectedPosition);
        if (piece == null) {
            return new ArrayList<>();
        }
        
        List<Position> possibleMoves = piece.getPossibleMoves(board);
        List<Position> legalMoves = new ArrayList<>();
        
        // Filtra apenas movimentos que não colocam o rei em xeque
        for (Position move : possibleMoves) {
            Move testMove = new Move(selectedPosition, move, piece);
            if (!wouldPutKingInCheck(testMove)) {
                legalMoves.add(move);
            }
        }
        
        return legalMoves;
    }
    
    /**
     * Habilita ou desabilita a IA
     */
    public void setAIEnabled(boolean enabled) {
        this.aiEnabled = enabled;
    }
    
    /**
     * Verifica se a IA está habilitada
     */
    public boolean isAIEnabled() {
        return aiEnabled;
    }
    
    /**
     * Define a dificuldade da IA
     */
    public void setAIDifficulty(int difficulty) {
        ai.setDifficulty(difficulty);
    }
    
    /**
     * Obtém a dificuldade da IA
     */
    public int getAIDifficulty() {
        return ai.getDifficulty();
    }
    
    /**
     * Faz o movimento da IA
     */
    private void makeAIMove() {
        Move aiMove = ai.makeBestMove(this);
        if (aiMove != null) {
            // Executa o movimento da IA
            executeMove(aiMove);
            
            // Adiciona ao histórico
            moveHistory.add(aiMove);
            
            // Verifica fim do jogo
            checkGameEnd();
            
            // Troca o turno de volta para o jogador
            isWhiteTurn = !isWhiteTurn;
        }
    }
}
