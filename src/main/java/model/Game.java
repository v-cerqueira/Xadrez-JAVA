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
    private IASuprema iaSuprema;
    private IANivel8 iaNivel8;
    private int advancedAILevel = 0; // 0: normal, 1: suprema, 2: suprema2

    /**
     * Define o nível da IA avançada (0: normal, 1: suprema, 2: suprema2)
     */
    public void setAdvancedAILevel(int level) {
        this.advancedAILevel = level;
    }

    /**
     * Obtém o nível da IA avançada
     */
    public int getAdvancedAILevel() {
        return advancedAILevel;
    }
    private boolean aiEnabled;
    private boolean aiSupreme; // modo IA Suprema
    private int halfmoveClock; // contador para regra dos 50 movimentos
    private java.util.Map<String, Integer> positionCount; // para repetição tripla
    
    public Game() {
        this.board = new Board();
        this.isWhiteTurn = true;
        this.moveHistory = new ArrayList<>();
        this.selectedPosition = null;
        this.gameOver = false;
        this.gameResult = null;

        this.ai = new ChessAI();
        this.iaSuprema = new IASuprema();
        this.iaNivel8 = new IANivel8();
        this.aiEnabled = false;
        this.aiSupreme = false;
        this.halfmoveClock = 0;
        this.positionCount = new java.util.HashMap<>();
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

        // Regra real: não é permitido "comer" o rei. Movimentos que apontam para o rei adversário são ilegais.
        if (capturedPiece instanceof King) {
            return false;
        }
        
        // Verifica se o movimento coloca o próprio rei em xeque
        if (wouldPutKingInCheck(move)) {
            return false;
        }
        
        // Executa o movimento
        // Trata en passant: se peão move duas casas, marca alvo; se captura via en passant, remove peão capturado
        handleEnPassantPreMove(piece, move);
        executeMove(move);
        handleEnPassantPostMove(piece, move);
        
        // Atualiza contadores para regras de empate
        updateGameStateCounters(move);
        
        // Adiciona ao histórico
        moveHistory.add(move);
        
        // Troca o turno primeiro
        isWhiteTurn = !isWhiteTurn;
        selectedPosition = null;
        
        // Verifica fim do jogo do lado que vai jogar agora
        checkGameEnd();
        
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

    // Marca/consome estado de en passant
    private void handleEnPassantPreMove(Piece piece, Move move) {
        // Reset padrão
        board.setEnPassantTarget(null);
        if (piece instanceof Pawn) {
            int rowDiff = Math.abs(move.getTo().getRow() - move.getFrom().getRow());
            int colDiff = Math.abs(move.getTo().getCol() - move.getFrom().getCol());
            // Duplo passo: marca casa atrás do peão como alvo
            if (colDiff == 0 && rowDiff == 2) {
                int midRow = (move.getFrom().getRow() + move.getTo().getRow()) / 2;
                board.setEnPassantTarget(new Position(midRow, move.getFrom().getCol()));
            }
        }
    }

    private void handleEnPassantPostMove(Piece piece, Move move) {
        if (piece instanceof Pawn) {
            // Captura en passant: destino vazio, mas diagonal e alvo coincide
            Position ep = board.getEnPassantTarget();
            // Como já movemos a peça e resetamos antes, precisamos inferir: se movimento foi diagonal sem peça capturada originalmente
            if (move.getPieceCaptured() == null) {
                int rowDiff = Math.abs(move.getTo().getRow() - move.getFrom().getRow());
                int colDiff = Math.abs(move.getTo().getCol() - move.getFrom().getCol());
                if (rowDiff == 1 && colDiff == 1) {
                    // Remove o peão capturado atrás do destino
                    int direction = piece.isWhite() ? 1 : -1; // peão capturado está uma casa "atrás"
                    Position capturedPos = new Position(move.getTo().getRow() + direction, move.getTo().getCol());
                    Piece maybePawn = board.getPieceAt(capturedPos);
                    if (maybePawn instanceof Pawn && maybePawn.isWhite() != piece.isWhite()) {
                        board.removePieceAt(capturedPos);
                        move.setEnPassant(true);
                    }
                }
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
        } else if (halfmoveClock >= 50) {
            gameOver = true;
            gameResult = "Empate por regra dos 50 movimentos!";
        } else if (isThreefoldRepetition()) {
            gameOver = true;
            gameResult = "Empate por repetição tripla!";
        }
    }
    
    private boolean isThreefoldRepetition() {
        String currentKey = generatePositionKey();
        return positionCount.getOrDefault(currentKey, 0) >= 3;
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
        this.halfmoveClock = 0;
        this.positionCount.clear();
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
            // Regra real: não permitir movimentos que capturam o rei adversário
            Piece target = board.getPieceAt(move);
            if (target instanceof King) {
                continue;
            }
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
     * Habilita/desabilita o modo IA Suprema
     */
    public void setAISupremeMode(boolean enabled) {
        this.aiSupreme = enabled;
        this.ai.setSupremeMode(enabled);
        if (enabled) {
            // Ajusta a dificuldade para 10 ao ativar o modo supremo
            this.ai.setDifficulty(10);
        }
    }

    /**
     * Verifica se o modo IA Suprema está habilitado
     */
    public boolean isAISupremeMode() {
        return aiSupreme;
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
        Move aiMove;
        if (advancedAILevel == 1) {
            // IA Suprema
            aiMove = iaSuprema.chooseBestMove(board, false);
        } else if (advancedAILevel == 2) {
            // IA Suprema 2 (Quiescência)
            aiMove = iaNivel8.chooseBestMove(board, false);
        } else {
            aiMove = ai.makeBestMove(this);
        }
        if (aiMove != null) {
            // Rejeita movimentos que capturam o rei ou deixam o rei da IA em xeque
            Piece target = board.getPieceAt(aiMove.getTo());
            boolean capturesKing = (target instanceof King);
            boolean putsOwnKingInCheck = wouldPutKingInCheck(new Move(aiMove.getFrom(), aiMove.getTo(), aiMove.getPieceMoved()));

            if (capturesKing || putsOwnKingInCheck) {
                // Procura um movimento legal alternativo simples
                Move fallback = findAnyLegalMove(false); // IA joga de pretas
                if (fallback == null) {
                    // Sem movimentos legais: estado de mate/afogamento será detectado
                    return;
                }
                aiMove = fallback;
            }

            // Executa o movimento da IA
            executeMove(aiMove);

            // Adiciona ao histórico
            moveHistory.add(aiMove);

            // Troca o turno de volta para o jogador
            isWhiteTurn = !isWhiteTurn;

            // Verifica fim do jogo do lado que vai jogar agora
            checkGameEnd();

            // Se modo supremo permanecer ligado e ainda for turno da IA após alternância
            if (aiEnabled && ai.shouldMakeMove(this)) {
                // Chama recursivamente para permitir sequência caso necessário
                makeAIMove();
            }
        }
    }

    // Encontra qualquer movimento legal para a cor indicada (simples)
    private Move findAnyLegalMove(boolean isWhite) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPieceAt(new Position(row, col));
                if (piece != null && piece.isWhite() == isWhite) {
                    List<Position> possible = piece.getPossibleMoves(board);
                    for (Position to : possible) {
                        Piece target = board.getPieceAt(to);
                        if (target instanceof King) {
                            continue; // não capturar rei
                        }
                        Move m = new Move(piece.getPosition(), to, piece);
                        if (!wouldPutKingInCheck(m)) {
                            return m;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private void updateGameStateCounters(Move move) {
        // Regra dos 50 movimentos: reset se peão move ou peça é capturada
        if (move.getPieceMoved() instanceof Pawn || move.getPieceCaptured() != null) {
            halfmoveClock = 0;
        } else {
            halfmoveClock++;
        }

        // Repetição tripla: conta posições
        String positionKey = generatePositionKey();
        positionCount.put(positionKey, positionCount.getOrDefault(positionKey, 0) + 1);
    }

    private String generatePositionKey() {
        // Chave simples: peças + en passant + roque
        StringBuilder key = new StringBuilder();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPieceAt(new Position(row, col));
                if (piece == null) {
                    key.append(".");
                } else {
                    key.append(piece.getSymbol());
                }
            }
        }
        // Adiciona estado de en passant e roque
        Position ep = board.getEnPassantTarget();
        key.append(ep != null ? ep.toString() : "null");
        key.append(isWhiteTurn ? "W" : "B");
        return key.toString();
    }
}
