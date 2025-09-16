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
    private boolean supremeMode; // Quando ativo, usa avaliação "neural"
    private final NeuralEvaluator neuralEvaluator;
    
    public ChessAI() {
        this.random = new Random();
        this.difficulty = 2; // dificuldade padrão
        this.supremeMode = false;
        this.neuralEvaluator = new NeuralEvaluator();
    }
    
    public ChessAI(int difficulty) {
        this.random = new Random();
        this.difficulty = clampDifficulty(difficulty);
        this.supremeMode = false;
        this.neuralEvaluator = new NeuralEvaluator();
    }
    
    /**
     * Define a dificuldade da IA
     */
    public void setDifficulty(int difficulty) {
        this.difficulty = clampDifficulty(difficulty);
    }
    
    /**
     * Obtém a dificuldade atual
     */
    public int getDifficulty() {
        return difficulty;
    }

    /**
     * Habilita/desabilita o modo IA Suprema (avaliação neural fictícia)
     */
    public void setSupremeMode(boolean enabled) {
        this.supremeMode = enabled;
    }

    /**
     * Verifica se o modo IA Suprema está habilitado
     */
    public boolean isSupremeMode() {
        return supremeMode;
    }
    
    /**
     * Indica se o avaliador neural está usando um modelo DL4J carregado
     */
    public boolean isSupremeUsingDL4J() {
        return neuralEvaluator != null && neuralEvaluator.isDl4jAvailable();
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
        
        if (supremeMode) {
            System.out.println("[IA Suprema] Avaliação neural + busca em profundidade 4 + move ordering ativadas.");
        }

        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        
        // Move ordering para IA Suprema: ordena por capturas primeiro
        if (supremeMode) {
            allPossibleMoves.sort((m1, m2) -> {
                int score1 = getMoveOrderingScore(m1);
                int score2 = getMoveOrderingScore(m2);
                return Integer.compare(score2, score1); // Maior score primeiro
            });
        }
        
        for (Move move : allPossibleMoves) {
            // Simula o movimento
            Board tempBoard = board.copy();
            tempBoard.movePiece(move.getFrom(), move.getTo());

            int score;
            if (supremeMode) {
                // Busca em profundidade 4 com move ordering
                score = minimax(tempBoard, 4, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
            } else {
                // Avaliação estática simples
                score = evaluate(tempBoard, false);
            }

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        
        // Adiciona aleatoriedade baseada na dificuldade (desliga no modo supremo)
        if (!supremeMode && difficulty == 1) {
            // 50% de chance de fazer um movimento aleatório
            if (random.nextDouble() < 0.5) {
                bestMove = allPossibleMoves.get(random.nextInt(allPossibleMoves.size()));
            }
        } else if (!supremeMode && difficulty == 2) {
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
                    
                    // Adiciona valor posicional para IA Suprema
                    if (supremeMode) {
                        pieceValue += getPositionalValue(piece, pos);
                    }
                    
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
    
    // Valor posicional para tornar a IA mais inteligente
    private int getPositionalValue(Piece piece, Position pos) {
        int value = 0;
        
        // Peões: centro vale mais
        if (piece instanceof Pawn) {
            int centerDistance = Math.abs(pos.getCol() - 3) + Math.abs(pos.getCol() - 4);
            value += (8 - centerDistance) * 5;
            
            // Peões avançados valem mais
            if (piece.isWhite()) {
                value += (7 - pos.getRow()) * 10;
            } else {
                value += pos.getRow() * 10;
            }
        }
        
        // Cavalos: centro vale mais
        if (piece instanceof Knight) {
            int centerDistance = Math.abs(pos.getCol() - 3) + Math.abs(pos.getCol() - 4) + 
                               Math.abs(pos.getRow() - 3) + Math.abs(pos.getRow() - 4);
            value += (16 - centerDistance) * 3;
        }
        
        // Bispos: diagonais longas
        if (piece instanceof Bishop) {
            if ((pos.getRow() + pos.getCol()) % 2 == 0) {
                value += 20; // Diagonal principal
            }
        }
        
        // Torres: colunas abertas
        if (piece instanceof Rook) {
            // Simplificado: torres no centro valem mais
            int centerDistance = Math.abs(pos.getCol() - 3) + Math.abs(pos.getCol() - 4);
            value += (8 - centerDistance) * 5;
        }
        
        return value;
    }

    /**
     * Decide qual avaliação usar com base na dificuldade e modo supremo
     */
    private int evaluate(Board board, boolean isWhite) {
        if (supremeMode || difficulty >= 10) {
            return neuralEvaluator.evaluate(board, isWhite);
        }
        return evaluatePosition(board, isWhite);
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

    private int clampDifficulty(int value) {
        // Permite 1..10
        return Math.max(1, Math.min(10, value));
    }

    /**
     * Avaliador "neural" fictício para demonstração do modo supremo.
     * Converte o tabuleiro para um vetor simples e usa uma função
     * determinística para simular uma predição.
     */
    private static class NeuralEvaluator {
        // Backend DL4J (opcional): se um modelo for fornecido e as libs estiverem no classpath, usa; senão, cai para MLP embutido
        private Object dl4jModel; // evitamos referências diretas a tipos DL4J para compilar sem as libs
        private boolean dl4jAvailable;
        private final int inputSize = 12 * 64;
        private final int hiddenSize = 64;
        private final float[] w1; // [hiddenSize * inputSize]
        private final float[] b1; // [hiddenSize]
        private final float[] w2; // [hiddenSize]
        private final float b2;

        NeuralEvaluator() {
            // Inicializa pesos de forma determinística (sem dependências externas)
            Random rng = new Random(42L);
            w1 = new float[hiddenSize * inputSize];
            b1 = new float[hiddenSize];
            w2 = new float[hiddenSize];
            for (int i = 0; i < w1.length; i++) w1[i] = (rng.nextFloat() - 0.5f) * 0.05f;
            for (int i = 0; i < b1.length; i++) b1[i] = (rng.nextFloat() - 0.5f) * 0.05f;
            for (int i = 0; i < w2.length; i++) w2[i] = (rng.nextFloat() - 0.5f) * 0.05f;
            b2 = (rng.nextFloat() - 0.5f) * 0.05f;
            // Tenta carregar modelo DL4J via reflexão, se presente em resources (ex: models/chess_mlp.zip)
            try {
                java.io.InputStream is = getClass().getClassLoader().getResourceAsStream("models/chess_mlp.zip");
                if (is != null) {
                    java.io.File temp = java.io.File.createTempFile("chess_mlp", ".zip");
                    java.nio.file.Files.copy(is, temp.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    Class<?> modelSerializerClass = Class.forName("org.deeplearning4j.util.ModelSerializer");
                    java.lang.reflect.Method restore = modelSerializerClass.getMethod("restoreMultiLayerNetwork", java.io.File.class);
                    dl4jModel = restore.invoke(null, temp);
                    temp.deleteOnExit();
                }
            } catch (Throwable ignore) {
                dl4jModel = null;
            }
            dl4jAvailable = (dl4jModel != null);
        }

        int evaluate(Board board, boolean isWhite) {
            float[] input = toInput(board);
            float prediction = predict(input);
            // Converte para escala tradicional
            int score = Math.round(prediction * 100);
            // Perspectiva do jogador (positivo bom para "isWhite")
            return isWhite ? score : -score;
        }

        private float[] toInput(Board board) {
            // 12 planos x 64 (P,C,B,T,D,R)x(Brancas,Pretas) de forma simplificada
            float[] input = new float[12 * 64];
            int idx = 0;
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    Position pos = new Position(row, col);
                    Piece piece = board.getPieceAt(pos);
                    // Mapeamento simples de peças
                    int plane = pieceToPlane(piece);
                    for (int p = 0; p < 12; p++) {
                        input[idx++] = (p == plane) ? 1.0f : 0.0f;
                    }
                }
            }
            return input;
        }

        private int pieceToPlane(Piece piece) {
            if (piece == null) return -1;
            int base = piece.isWhite() ? 0 : 6;
            if (piece instanceof Pawn) return base + 0;
            if (piece instanceof Knight) return base + 1;
            if (piece instanceof Bishop) return base + 2;
            if (piece instanceof Rook) return base + 3;
            if (piece instanceof Queen) return base + 4;
            if (piece instanceof King) return base + 5;
            return -1;
        }

        private float predict(float[] input) {
            if (dl4jModel != null) {
                try {
                    // Usa DL4J via reflexão
                    Class<?> nd4jFactory = Class.forName("org.nd4j.linalg.factory.Nd4j");
                    java.lang.reflect.Method create = nd4jFactory.getMethod("create", float[].class);
                    Object in = create.invoke(null, (Object) input);
                    // reshape(1, input.length)
                    java.lang.reflect.Method reshape = in.getClass().getMethod("reshape", int.class, int.class);
                    in = reshape.invoke(in, 1, input.length);

                    java.lang.reflect.Method output = dl4jModel.getClass().getMethod("output", in.getClass(), boolean.class);
                    Object out = output.invoke(dl4jModel, in, false);
                    java.lang.reflect.Method getFloat = out.getClass().getMethod("getFloat", int.class);
                    return (float) getFloat.invoke(out, 0);
                } catch (Throwable ignore) {
                    // Falhou usar DL4J; cai para MLP embutido
                }
            }
            // MLP 1 camada oculta com tanh
            float[] h = new float[hiddenSize];
            int k = 0;
            for (int i = 0; i < hiddenSize; i++) {
                float sum = b1[i];
                // dot(w1[i], input)
                for (int j = 0; j < inputSize; j++) {
                    sum += w1[k++] * input[j];
                }
                h[i] = (float) Math.tanh(sum);
            }
            float out = b2;
            for (int i = 0; i < hiddenSize; i++) out += w2[i] * h[i];
            // Comprimi para faixa -1..1 via tanh final
            return (float) Math.tanh(out);
        }
        
        boolean isDl4jAvailable() { return dl4jAvailable; }
    }
    
    // Move ordering: prioriza capturas e movimentos que dão xeque
    private int getMoveOrderingScore(Move move) {
        int score = 0;
        
        // Capturas: peça capturada vale mais que peça que captura
        if (move.getPieceCaptured() != null) {
            score += getPieceValue(move.getPieceCaptured()) - getPieceValue(move.getPieceMoved());
        }
        
        // Movimentos de peão (avanço)
        if (move.getPieceMoved() instanceof Pawn) {
            score += 10;
        }
        
        // Movimentos de peças menores (cavalo, bispo)
        if (move.getPieceMoved() instanceof Knight || move.getPieceMoved() instanceof Bishop) {
            score += 5;
        }
        
        return score;
    }
    
    // Minimax com poda alfa-beta para IA Suprema
    private int minimax(Board board, int depth, boolean maximizingPlayer, int alpha, int beta) {
        if (depth == 0) {
            return evaluate(board, false);
        }
        
        boolean isWhite = maximizingPlayer;
        List<Move> moves = getAllPossibleMoves(board, isWhite);
        
        if (moves.isEmpty()) {
            return evaluate(board, false);
        }
        
        // Move ordering também na busca recursiva
        if (supremeMode && depth > 1) {
            moves.sort((m1, m2) -> {
                int score1 = getMoveOrderingScore(m1);
                int score2 = getMoveOrderingScore(m2);
                return Integer.compare(score2, score1);
            });
        }
        
        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : moves) {
                Board newBoard = board.copy();
                newBoard.movePiece(move.getFrom(), move.getTo());
                int eval = minimax(newBoard, depth - 1, false, alpha, beta);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break; // Poda beta
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : moves) {
                Board newBoard = board.copy();
                newBoard.movePiece(move.getFrom(), move.getTo());
                int eval = minimax(newBoard, depth - 1, true, alpha, beta);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break; // Poda alfa
            }
            return minEval;
        }
    }
}
