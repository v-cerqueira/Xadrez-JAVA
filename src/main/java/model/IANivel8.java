package model;

import java.util.ArrayList;
import java.util.List;

public class IANivel8 {
    private static final int PROFUNDIDADE_MAXIMA = 3; // Profundidade reduzida para melhorar velocidade

    public IANivel8() {}

    // Negamax com busca de quiescência
    public Move chooseBestMove(Board board, boolean isWhite) {
        int cor = isWhite ? 1 : -1;
        List<Move> moves = getAllPossibleMoves(board, isWhite);
        if (moves.isEmpty()) return null;
        Move melhorMovimento = null;
        int melhorAvaliacao = Integer.MIN_VALUE;
        for (Move move : moves) {
            Board tabuleiroSimulado = board.copy();
            tabuleiroSimulado.movePiece(move.getFrom(), move.getTo());
            int avaliacao = -negamax(tabuleiroSimulado, PROFUNDIDADE_MAXIMA - 1, -cor, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (avaliacao > melhorAvaliacao) {
                melhorAvaliacao = avaliacao;
                melhorMovimento = move;
            }
        }
        return melhorMovimento;
    }

    private int negamax(Board board, int profundidade, int cor, int alfa, int beta) {
        if (profundidade == 0) {
            return quiescence(board, cor, alfa, beta);
        }
        List<Move> moves = getAllPossibleMoves(board, cor == 1);
        if (moves.isEmpty()) {
            return cor * avaliarTabuleiro(board, cor == 1);
        }
        int valorMax = Integer.MIN_VALUE;
        for (Move move : moves) {
            Board tabuleiroSimulado = board.copy();
            tabuleiroSimulado.movePiece(move.getFrom(), move.getTo());
            int valor = -negamax(tabuleiroSimulado, profundidade - 1, -cor, -beta, -alfa);
            if (valor > valorMax) {
                valorMax = valor;
            }
            if (valorMax > alfa) {
                alfa = valorMax;
            }
            if (alfa >= beta) {
                break;
            }
        }
        return valorMax;
    }

    // Busca de quiescência: só avalia capturas até posição "quieta"
    private int quiescence(Board board, int cor, int alfa, int beta) {
        int avaliacao = cor * avaliarTabuleiro(board, cor == 1);
        if (avaliacao >= beta) {
            return beta;
        }
        if (avaliacao > alfa) {
            alfa = avaliacao;
        }
        List<Move> capturas = getCaptureMoves(board, cor == 1);
        for (Move captura : capturas) {
            Board tabuleiroSimulado = board.copy();
            tabuleiroSimulado.movePiece(captura.getFrom(), captura.getTo());
            int valor = -quiescence(tabuleiroSimulado, -cor, -beta, -alfa);
            if (valor >= beta) {
                return beta;
            }
            if (valor > alfa) {
                alfa = valor;
            }
        }
        return alfa;
    }

    // Gera todos os movimentos possíveis para uma cor
    private List<Move> getAllPossibleMoves(Board board, boolean isWhite) {
        List<Move> moves = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position pos = new Position(row, col);
                model.pieces.Piece piece = board.getPieceAt(pos);
                if (piece != null && piece.isWhite() == isWhite) {
                    List<Position> possiblePositions = piece.getPossibleMoves(board);
                    for (Position target : possiblePositions) {
                        Move move = new Move(pos, target, piece);
                        model.pieces.Piece captured = board.getPieceAt(target);
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

    // Gera apenas movimentos de captura
    private List<Move> getCaptureMoves(Board board, boolean isWhite) {
        List<Move> moves = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position pos = new Position(row, col);
                model.pieces.Piece piece = board.getPieceAt(pos);
                if (piece != null && piece.isWhite() == isWhite) {
                    List<Position> possiblePositions = piece.getPossibleMoves(board);
                    for (Position target : possiblePositions) {
                        model.pieces.Piece captured = board.getPieceAt(target);
                        if (captured != null && captured.isWhite() != isWhite) {
                            Move move = new Move(pos, target, piece);
                            move.setPieceCaptured(captured);
                            moves.add(move);
                        }
                    }
                }
            }
        }
        return moves;
    }

    // Função de avaliação adaptada
    private int avaliarTabuleiro(Board board, boolean isWhite) {
        int score = 0;
        // Soma material
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                model.pieces.Piece piece = board.getPieceAt(new Position(row, col));
                if (piece != null) {
                    int value = getPieceValue(piece);
                    score += (piece.isWhite() == isWhite) ? value : -value;
                }
            }
        }
        // Mobilidade
        int mobilidade = 0;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                model.pieces.Piece piece = board.getPieceAt(new Position(row, col));
                if (piece != null && piece.isWhite() == isWhite) {
                    mobilidade += piece.getPossibleMoves(board).size();
                }
            }
        }
        score += mobilidade * 10;
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
    private int getPieceValue(model.pieces.Piece piece) {
        if (piece instanceof model.pieces.Pawn) return 100;
        if (piece instanceof model.pieces.Knight || piece instanceof model.pieces.Bishop) return 300;
        if (piece instanceof model.pieces.Rook) return 500;
        if (piece instanceof model.pieces.Queen) return 900;
        if (piece instanceof model.pieces.King) return 10000;
        return 0;
    }
}
