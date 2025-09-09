package model;

import model.pieces.*;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

/**
 * IA simples para o jogo de xadrez
 */
public class ChessAI {
    private Random random;
    private int difficulty; // 1-10, onde 1 é mais fácil e 10 é mais difícil
    
    public ChessAI() {
        this.random = new Random();
        this.difficulty = 2; // dificuldade padrão
    }
    
    public ChessAI(int difficulty) {
        this.random = new Random();
        this.difficulty = Math.max(1, Math.min(3, difficulty));
    }
    
    /**
     * Define a dificuldade da IA
     */
    public void setDifficulty(int difficulty) {
        this.difficulty = Math.max(1, Math.min(3, difficulty));
    }
    
    /**
     * Obtém a dificuldade atual
     */
    public int getDifficulty() {
        return difficulty;
    }
    
    /**
     * Faz o melhor movimento possível para as peças pretas
     */
    public Move makeBestMove(Game game) {
        if (game.isWhiteTurn()) {
            return null; // Não é o turno da IA
        }
        
        Board board = game.getBoard();
        List<Move> allPossibleMoves = getAllPossibleMoves(board, false);
        
        if (allPossibleMoves.isEmpty()) {
            return null; // Não há movimentos possíveis
        }
        
        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        
        for (Move move : allPossibleMoves) {
            // Simula o movimento
            Board tempBoard = board.copy();
            tempBoard.movePiece(move.getFrom(), move.getTo());
            
            // Avalia a posição resultante
            int score = evaluatePosition(tempBoard, false);
            
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        
        // Adiciona aleatoriedade baseada na dificuldade
        if (difficulty == 1) {
            // 50% de chance de fazer um movimento aleatório
            if (random.nextDouble() < 0.5) {
                bestMove = allPossibleMoves.get(random.nextInt(allPossibleMoves.size()));
            }
        } else if (difficulty == 2) {
            // 20% de chance de fazer um movimento aleatório
            if (random.nextDouble() < 0.2) {
                bestMove = allPossibleMoves.get(random.nextInt(allPossibleMoves.size()));
            }
        }
        // Dificuldade 3 sempre faz o melhor movimento
        
        return bestMove;
    }
    
    /**
     * Obtém todos os movimentos possíveis para uma cor
     */
    private List<Move> getAllPossibleMoves(Board board, boolean isWhite) {
        List<Move> moves = new ArrayList<>();
        
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position pos = new Position(row, col);
                Piece piece = board.getPieceAt(pos);
                
                if (piece != null && piece.isWhite() == isWhite) {
                    List<Position> possiblePositions = piece.getPossibleMoves(board);
                    
                    for (Position target : possiblePositions) {
                        Move move = new Move(pos, target, piece);
                        Piece captured = board.getPieceAt(target);
                        if (captured != null) {
                            move.setPieceCaptured(captured);
                        }
                        moves.add(move);
                    }
                }
            }
        }
        
        return moves;
    }
    
    /**
     * Avalia uma posição do tabuleiro
     */
    private int evaluatePosition(Board board, boolean isWhite) {
        int score = 0;
        
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position pos = new Position(row, col);
                Piece piece = board.getPieceAt(pos);
                
                if (piece != null) {
                    int pieceValue = getPieceValue(piece);
                    if (piece.isWhite() == isWhite) {
                        score += pieceValue;
                    } else {
                        score -= pieceValue;
                    }
                }
            }
        }
        
        return score;
    }
    
    /**
     * Retorna o valor de uma peça
     */
    private int getPieceValue(Piece piece) {
        if (piece instanceof Pawn) {
            return 100;
        } else if (piece instanceof Knight || piece instanceof Bishop) {
            return 300;
        } else if (piece instanceof Rook) {
            return 500;
        } else if (piece instanceof Queen) {
            return 900;
        } else if (piece instanceof King) {
            return 10000;
        }
        return 0;
    }
    
    /**
     * Verifica se a IA deve fazer um movimento
     */
    public boolean shouldMakeMove(Game game) {
        return !game.isWhiteTurn() && !game.isGameOver();
    }
}
