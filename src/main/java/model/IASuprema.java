package model;

import model.pieces.Piece; // Import adicionado para corrigir erro de tipo não resolvido

public class IASuprema {
    private static final int DEFAULT_DEPTH = 3; // Profundidade reduzida para melhorar velocidade
    private static final int PROFUNDIDADE_MAXIMA = 5;

    public IASuprema() {}

    // Escolhe o melhor movimento usando minimax
    public Move chooseBestMove(Board board, boolean isWhite) {
        java.util.List<Move> moves = getAllPossibleMoves(board, isWhite);
        if (moves.isEmpty()) return null;
        Move melhorMovimento = null;
        int melhorAvaliacao = isWhite ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (Move move : moves) {
            Board tabuleiroSimulado = board.copy();
            tabuleiroSimulado.movePiece(move.getFrom(), move.getTo());
            int avaliacao = minimax(tabuleiroSimulado, DEFAULT_DEPTH - 1, !isWhite, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if ((isWhite && avaliacao > melhorAvaliacao) || (!isWhite && avaliacao < melhorAvaliacao)) {
                melhorAvaliacao = avaliacao;
                melhorMovimento = move;
            }
        }
        return melhorMovimento;
    }

    // Gera todos os movimentos possíveis para uma cor
    private java.util.List<Move> getAllPossibleMoves(Board board, boolean isWhite) {
        java.util.List<Move> moves = new java.util.ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position pos = new Position(row, col);
                Piece piece = board.getPieceAt(pos);
                if (piece != null && piece.isWhite() == isWhite) {
                    java.util.List<Position> possiblePositions = piece.getPossibleMoves(board);
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

    // Função de avaliação adaptada
    private int evaluate(Board board, boolean isWhite) {
        int score = 0;
        // Soma material
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPieceAt(new Position(row, col));
                if (piece != null) {
                    int value = getPieceValue(piece);
                    score += (piece.isWhite() == isWhite) ? value : -value;
                }
            }
        }
        // Mobilidade
        int mobility = 0;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPieceAt(new Position(row, col));
                if (piece != null && piece.isWhite() == isWhite) {
                    mobility += piece.getPossibleMoves(board).size();
                }
            }
        }
        score += mobility * 10;
        // Segurança do rei
        Position kingPos = board.findKing(isWhite);
        if (kingPos != null && board.isSquareAttacked(kingPos, !isWhite)) {
            score -= 50;
        }
        Position oppKingPos = board.findKing(!isWhite);
        if (oppKingPos != null && board.isSquareAttacked(oppKingPos, isWhite)) {
            score += 50;
        }
        return score;
    }

    // Valor das peças
    private int getPieceValue(Piece piece) {
        switch (piece.getName()) {
            case "Peão": return 100;
            case "Cavalo": return 320;
            case "Bispo": return 330;
            case "Torre": return 500;
            case "Rainha": return 900;
            case "Rei": return 20000;
            default: return 0;
        }
    }

    // Minimax com poda alfa-beta
    private int minimax(Board board, int profundidade, boolean isWhite, int alfa, int beta) {
        if (profundidade == 0) {
            return evaluate(board, isWhite);
        }
        java.util.List<Move> moves = getAllPossibleMoves(board, isWhite);
        if (moves.isEmpty()) {
            return evaluate(board, isWhite);
        }
        if (isWhite) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : moves) {
                Board novoTabuleiro = board.copy();
                novoTabuleiro.movePiece(move.getFrom(), move.getTo());
                int eval = minimax(novoTabuleiro, profundidade - 1, false, alfa, beta);
                maxEval = Math.max(maxEval, eval);
                alfa = Math.max(alfa, eval);
                if (beta <= alfa) break;
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : moves) {
                Board novoTabuleiro = board.copy();
                novoTabuleiro.movePiece(move.getFrom(), move.getTo());
                int eval = minimax(novoTabuleiro, profundidade - 1, true, alfa, beta);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alfa) break;
            }
            return minEval;
        }
    }
}
