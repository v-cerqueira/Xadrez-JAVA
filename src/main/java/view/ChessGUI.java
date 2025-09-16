package view;

import model.*;
import model.pieces.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Interface gráfica principal do jogo de xadrez
 */
public class ChessGUI extends JFrame {
    private Game game;
    private JButton[][] boardButtons;
    private JLabel statusLabel;
    private JLabel aiModeLabel;
    private JTextArea moveHistoryArea;
    private JButton newGameButton;
    private JCheckBox aiCheckBox;
    private JComboBox<String> difficultyComboBox;
    private JButton supremeAIButton;
    
    // Cores do tabuleiro - Tema Futurista
    private static final Color LIGHT_SQUARE = new Color(30, 30, 50);        // Azul escuro futurista
    private static final Color DARK_SQUARE = new Color(10, 10, 25);         // Preto azulado profundo
    private static final Color SELECTED_SQUARE = new Color(0, 255, 255);    // Ciano neon brilhante
    private static final Color POSSIBLE_MOVE = new Color(255, 0, 255);      // Magenta neon
    
    public ChessGUI() {
        this.game = new Game();
        initializeGUI();
        updateBoard();
        updateStatus();
    }
    
    /**
     * Inicializa a interface gráfica
     */
    private void initializeGUI() {
        setTitle("Jogo de Xadrez");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(15, 15, 35)); // Fundo futurista da janela
        
        // Painel principal do tabuleiro
        JPanel boardPanel = createBoardPanel();
        add(boardPanel, BorderLayout.CENTER);
        
        // Painel lateral com controles e informações
        JPanel sidePanel = createSidePanel();
        add(sidePanel, BorderLayout.EAST);
        
        pack();
        setLocationRelativeTo(null);
        setResizable(true);
    }
    
    /**
     * Cria o painel do tabuleiro
     */
    private JPanel createBoardPanel() {
        JPanel panel = new JPanel(new GridLayout(8, 8));
        panel.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 255), 3)); // Borda ciano futurista
        panel.setBackground(new Color(20, 20, 40)); // Fundo escuro futurista
        
        boardButtons = new JButton[8][8];
        
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(40, 40));
                button.setFont(new Font("Arial", Font.BOLD, 24));
                
                // Alterna cores das casas com tema futurista
                if ((row + col) % 2 == 0) {
                    button.setBackground(LIGHT_SQUARE);
                } else {
                    button.setBackground(DARK_SQUARE);
                }
                
                button.setOpaque(true);
                button.setBorderPainted(true);
                button.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 150), 1)); // Borda azul sutil
                
                final int finalRow = row;
                final int finalCol = col;
                
                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        handleSquareClick(finalRow, finalCol);
                    }
                });
                
                boardButtons[row][col] = button;
                panel.add(button);
            }
        }
        
        return panel;
    }
    
    /**
     * Cria o painel lateral com controles
     */
    private JPanel createSidePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(250, 0));
        panel.setBackground(new Color(25, 25, 45)); // Fundo futurista
        
        // Status do jogo
        statusLabel = new JLabel("Turno: Brancas");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(new Color(0, 255, 255)); // Texto ciano futurista
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(statusLabel);
        
        // Indicador do modo de IA
        aiModeLabel = new JLabel("IA: Desativada");
        aiModeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        aiModeLabel.setForeground(new Color(255, 0, 255));
        aiModeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(aiModeLabel);

        panel.add(Box.createVerticalStrut(20));
        
        // Botões de controle
        newGameButton = new JButton("Novo Jogo");
        newGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        newGameButton.setOpaque(true);
        newGameButton.setContentAreaFilled(true);
        newGameButton.setFocusPainted(false);
        newGameButton.setBackground(new Color(25, 25, 45)); // Mesmo fundo do painel lateral
        newGameButton.setForeground(new Color(25, 25, 45)); // Texto igual ao fundo (invisível)
        newGameButton.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 255), 1)); // Borda ciano mais sutil
        newGameButton.addActionListener(e -> {
            System.out.println("Botão Novo Jogo clicado!");
            newGame();
        });
        panel.add(newGameButton);
        
        panel.add(Box.createVerticalStrut(20));
        
        // Controles da IA
        JLabel aiLabel = new JLabel("Jogar contra IA:");
        aiLabel.setForeground(new Color(255, 0, 255)); // Texto magenta futurista
        aiLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(aiLabel);
        
        aiCheckBox = new JCheckBox("Habilitar IA");
        aiCheckBox.setOpaque(true);
        aiCheckBox.setContentAreaFilled(false);
        aiCheckBox.setFocusPainted(false);
        aiCheckBox.setBackground(new Color(25, 25, 45)); // Mesmo fundo do painel lateral
        aiCheckBox.setForeground(new Color(0, 255, 255)); // Texto ciano
        aiCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        aiCheckBox.addActionListener(e -> {
            System.out.println("CheckBox IA clicado!");
            toggleAI();
        });
        panel.add(aiCheckBox);
        
        panel.add(Box.createVerticalStrut(10));
        
        JLabel difficultyLabel = new JLabel("Dificuldade da IA:");
        difficultyLabel.setForeground(new Color(255, 0, 255)); // Texto magenta futurista
        difficultyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(difficultyLabel);
        
        difficultyComboBox = new JComboBox<>(new String[]{"Nível 1", "Nível 2", "Nível 3", "Nível 4", "Nível 5", "Nível 6", "Nível 7", "Nível 8", "Nível 9", "Nível 10"});
        difficultyComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        difficultyComboBox.setOpaque(true);
        difficultyComboBox.setBackground(new Color(25, 25, 45)); // Mesmo fundo do painel lateral
        difficultyComboBox.setForeground(Color.WHITE); // Texto visível
        difficultyComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(new Color(25, 25, 45));
                setForeground(Color.WHITE); // Texto visível no dropdown
                return this;
            }
        });
        difficultyComboBox.setSelectedIndex(1); // Médio por padrão
        difficultyComboBox.addActionListener(e -> {
            System.out.println("ComboBox dificuldade alterado!");
            changeDifficulty();
        });
        panel.add(difficultyComboBox);
        
        panel.add(Box.createVerticalStrut(10));
        
        // Box para seleção de IA avançada
        JLabel advancedAILabel = new JLabel("IA Avançada:");
        advancedAILabel.setForeground(new Color(255, 0, 255));
        advancedAILabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(advancedAILabel);

        JComboBox<String> advancedAIComboBox = new JComboBox<>(new String[]{"Desativada", "IA Suprema", "IA Suprema 2 (Quiescência)"});
        advancedAIComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        advancedAIComboBox.setOpaque(true);
        advancedAIComboBox.setBackground(new Color(25, 25, 45));
        advancedAIComboBox.setForeground(Color.WHITE);
        advancedAIComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(new Color(25, 25, 45));
                setForeground(Color.WHITE);
                return this;
            }
        });
        advancedAIComboBox.setSelectedIndex(0);
        advancedAIComboBox.addActionListener(e -> {
            int idx = advancedAIComboBox.getSelectedIndex();
            if (idx == 1) {
                // IA Suprema
                game.setAISupremeMode(true);
                aiCheckBox.setSelected(true);
                game.setAIEnabled(true);
            } else if (idx == 2) {
                // IA Suprema 2 (Quiescência)
                game.setAISupremeMode(false);
                aiCheckBox.setSelected(true);
                game.setAIEnabled(true);
                game.setAdvancedAILevel(2); // método a ser implementado para ativar IANivel8
            } else {
                // Desativada
                game.setAISupremeMode(false);
                game.setAdvancedAILevel(0);
                aiCheckBox.setSelected(false);
                game.setAIEnabled(false);
            }
            updateAIModeLabel();
        });
        panel.add(advancedAIComboBox);

        panel.add(Box.createVerticalStrut(20));
        
        // Histórico de movimentos
        JLabel historyLabel = new JLabel("Histórico de Movimentos:");
        historyLabel.setForeground(new Color(255, 0, 255)); // Texto magenta futurista
        historyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(historyLabel);
        
        moveHistoryArea = new JTextArea(15, 20);
        moveHistoryArea.setEditable(false);
        moveHistoryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        moveHistoryArea.setBackground(new Color(20, 20, 40)); // Fundo escuro futurista
        moveHistoryArea.setForeground(new Color(0, 255, 255)); // Texto ciano
        moveHistoryArea.setCaretColor(new Color(0, 255, 255)); // Cursor ciano
        JScrollPane scrollPane = new JScrollPane(moveHistoryArea);
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 255), 1)); // Borda ciano
        panel.add(scrollPane);
        
        return panel;
    }
    
    /**
     * Trata o clique em uma casa do tabuleiro
     */
    private void handleSquareClick(int row, int col) {
        Position clickedPosition = new Position(row, col);
        
        if (game.getSelectedPosition() == null) {
            // Seleciona uma peça
            if (game.selectPiece(clickedPosition)) {
                updateBoard();
                updateStatus();
            }
        } else {
            // Tenta fazer um movimento
            if (game.makeMove(clickedPosition)) {
                updateBoard();
                updateStatus();
                updateMoveHistory();
            } else {
                // Se o movimento falhou, tenta selecionar outra peça
                if (game.selectPiece(clickedPosition)) {
                    updateBoard();
                    updateStatus();
                }
            }
        }
    }
    
    /**
     * Atualiza a exibição do tabuleiro
     */
    private void updateBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JButton button = boardButtons[row][col];
                Position position = new Position(row, col);
                Piece piece = game.getBoard().getPieceAt(position);
                
                // Define a cor de fundo base
                if ((row + col) % 2 == 0) {
                    button.setBackground(LIGHT_SQUARE);
                } else {
                    button.setBackground(DARK_SQUARE);
                }
                
                // Destaca a casa selecionada
                if (game.getSelectedPosition() != null && 
                    game.getSelectedPosition().equals(position)) {
                    button.setBackground(SELECTED_SQUARE);
                }
                
                // Destaca movimentos possíveis
                List<Position> possibleMoves = game.getPossibleMovesForSelected();
                if (possibleMoves.contains(position)) {
                    button.setBackground(POSSIBLE_MOVE);
                }
                
                // Define a imagem da peça
                if (piece != null) {
                    String pieceType = piece.getClass().getSimpleName();
                    ImageIcon pieceImage = PieceImages.getPieceImage(pieceType, piece.isWhite());
                    button.setIcon(pieceImage);
                    button.setText(""); // Remove o texto
                } else {
                    button.setIcon(null);
                    button.setText("");
                }
            }
        }
    }
    

    
    /**
     * Atualiza o status do jogo
     */
    private void updateStatus() {
        if (game.isGameOver()) {
            statusLabel.setText(game.getGameResult());
        } else {
            String turn = game.isWhiteTurn() ? "Brancas" : "Pretas";
            statusLabel.setText("Turno: " + turn);
            
            if (game.isKingInCheck(game.isWhiteTurn())) {
                statusLabel.setText("Turno: " + turn + " (XEQUE!)");
            }
            updateAIModeLabel();
        }
    }
    
    /**
     * Atualiza o histórico de movimentos
     */
    private void updateMoveHistory() {
        moveHistoryArea.setText("");
        List<Move> moves = game.getMoveHistory();
        
        for (int i = 0; i < moves.size(); i++) {
            Move move = moves.get(i);
            String moveNumber = (i / 2 + 1) + ".";
            if (i % 2 == 0) {
                moveHistoryArea.append(moveNumber + " " + getMoveNotation(move) + " ");
            } else {
                moveHistoryArea.append(getMoveNotation(move) + "\n");
            }
        }
    }
    
    /**
     * Converte um movimento para notação de xadrez
     */
    private String getMoveNotation(Move move) {
        String notation = "";
        Piece piece = move.getPieceMoved();
        
        if (!(piece instanceof Pawn)) {
            notation += piece.getSymbol();
        }
        
        notation += getPositionNotation(move.getFrom());
        
        if (move.getPieceCaptured() != null) {
            notation += "x";
        } else {
            notation += "-";
        }
        
        notation += getPositionNotation(move.getTo());
        
        if (move.getPromotionPiece() != null) {
            notation += "=" + move.getPromotionPiece().getSymbol();
        }
        
        return notation;
    }
    
    /**
     * Converte uma posição para notação de xadrez
     */
    private String getPositionNotation(Position position) {
        return (char)('a' + position.getCol()) + "" + (8 - position.getRow());
    }
    
    /**
     * Inicia um novo jogo
     */
    private void newGame() {
        System.out.println("Novo jogo iniciado!");
        game.resetGame();
        updateBoard();
        updateStatus();
        updateMoveHistory();
    }
    
    
    /**
     * Habilita ou desabilita a IA
     */
    private void toggleAI() {
        boolean aiEnabled = aiCheckBox.isSelected();
        game.setAIEnabled(aiEnabled);
        
        if (aiEnabled) {
            JOptionPane.showMessageDialog(this, 
                "IA habilitada! Você jogará contra o computador.\n" +
                "Dificuldade atual: " + difficultyComboBox.getSelectedItem());
            updateAIModeLabel();
        } else {
            JOptionPane.showMessageDialog(this, 
                "IA desabilitada! Você jogará contra outro jogador.");
            updateAIModeLabel();
        }
    }
    
    /**
     * Muda a dificuldade da IA
     */
    private void changeDifficulty() {
        System.out.println("Dificuldade alterada!");
        int difficulty = difficultyComboBox.getSelectedIndex() + 1;
        game.setAIDifficulty(difficulty);
        
        String difficultyName = difficultyComboBox.getSelectedItem().toString();
        JOptionPane.showMessageDialog(this, 
            "Dificuldade da IA alterada para: " + difficultyName);
        updateAIModeLabel();
    }
    
    /**
     * Liga/desliga o modo IA Suprema
     */
    private void toggleSupremeAI() {
        boolean enable = !game.isAISupremeMode();
        game.setAISupremeMode(enable);
        supremeAIButton.setText(enable ? "Desativar modo IA Suprema" : "Ativar modo IA Suprema");
        // Reflete visualmente a dificuldade 10 ao ativar o modo supremo
        if (enable) {
            difficultyComboBox.setSelectedIndex(9); // Nível 10
            aiCheckBox.setSelected(true); // Ativa a IA automaticamente
            game.setAIEnabled(true); // Garante que a IA está ativada
        }
        String msg = enable ?
            "Modo IA Suprema ativado! Minimax ativado automaticamente (dificuldade máxima)." :
            "Modo IA Suprema desativado. Voltando à IA tradicional.";
        JOptionPane.showMessageDialog(this, msg);
        updateAIModeLabel();
    }

    private void updateAIModeLabel() {
        String status = game.isAIEnabled() ? "Ativa" : "Desativada";
        if (game.isAIEnabled() && game.isAISupremeMode()) {
            aiModeLabel.setText("IA: Suprema (Neural) • Nível " + game.getAIDifficulty());
        } else if (game.isAIEnabled()) {
            aiModeLabel.setText("IA: Tradicional • Nível " + game.getAIDifficulty());
        } else {
            aiModeLabel.setText("IA: Desativada");
        }
    }
    
    /**
     * Método principal para executar o jogo
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Tenta usar o look and feel do sistema, se disponível
                String systemLookAndFeel = UIManager.getSystemLookAndFeelClassName();
                if (systemLookAndFeel != null) {
                    UIManager.setLookAndFeel(systemLookAndFeel);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            new ChessGUI().setVisible(true);
        });
    }
}
