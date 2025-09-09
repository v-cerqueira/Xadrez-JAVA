
package view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * Utilitário para carregar e gerenciar imagens das peças de xadrez
 */
public class PieceImages {
    private static final String IMAGE_BASE_PATH = "img peças/";
    private static final int PIECE_SIZE = 45;
    
    // Cache das imagens carregadas


    private static ImageIcon whiteKing, whiteQueen, whiteRook, whiteBishop, whiteKnight, whitePawn;
    private static ImageIcon blackKing, blackQueen, blackRook, blackBishop, blackKnight, blackPawn;
    
    static {
        loadImages();
    }
    
    /**
     * Carrega todas as imagens das peças
     */
    private static void loadImages() {
        try {

            // Carrega imagens das peças brancas (bco = branco)
            whiteKing = loadImage("rei bco.png");
            whiteQueen = loadImage("rainha bco.png");
            whiteRook = loadImage("torre bco.png");
            whiteBishop = loadImage("bispo bco.png");
            whiteKnight = loadImage("cavalo bco.png");
            whitePawn = loadImage("peao bco.png");
            
            // Carrega imagens das peças pretas (pto = preto)
            blackKing = loadImage("rei pto.png");
            blackQueen = loadImage("rainha pto.png");
            blackRook = loadImage("torre pto.png");
            blackBishop = loadImage("bispo pto.png");
            blackKnight = loadImage("cavalo pto.png");
            blackPawn = loadImage("peao pto.png");
            
        } catch (Exception e) {
            System.err.println("Erro ao carregar imagens das peças: " + e.getMessage());
            // As imagens fallback são criadas automaticamente no método loadImage
        }
    }
    
    /**
     * Carrega uma imagem da pasta local
     */
    private static ImageIcon loadImage(String filename) throws IOException {
        try {
            // Tenta carregar a imagem da pasta local
            String imagePath = IMAGE_BASE_PATH + filename;
            java.io.File imageFile = new java.io.File(imagePath);
            
            if (imageFile.exists()) {
                ImageIcon originalIcon = new ImageIcon(imageFile.getAbsolutePath());
                Image originalImage = originalIcon.getImage();
                Image scaledImage = originalImage.getScaledInstance(PIECE_SIZE, PIECE_SIZE, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            } else {
                // Se não encontrar, cria uma imagem simples
                return createSimpleImage(filename);
            }
        } catch (Exception e) {
            return createSimpleImage(filename);
        }
    }
    
    /**
     * Cria imagens simples como fallback quando não consegue carregar as imagens reais
     */
    private static ImageIcon createSimpleImage(String filename) {
        BufferedImage image = new BufferedImage(PIECE_SIZE, PIECE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // Habilita anti-aliasing para melhor qualidade
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        
        // Define a cor base da peça
        Color pieceColor = filename.startsWith("w") ? Color.WHITE : Color.BLACK;
        Color borderColor = filename.startsWith("w") ? Color.BLACK : Color.WHITE;
        
        // Desenha o fundo transparente
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fillRect(0, 0, PIECE_SIZE, PIECE_SIZE);
        
        // Desenha a peça baseada no tipo
        g2d.setColor(pieceColor);
        g2d.setStroke(new BasicStroke(1.5f));
        
        char pieceType = filename.charAt(1);
        switch (pieceType) {
            case 'K': // Rei
                drawKing(g2d, pieceColor, borderColor);
                break;
            case 'Q': // Rainha
                drawQueen(g2d, pieceColor, borderColor);
                break;
            case 'R': // Torre
                drawRook(g2d, pieceColor, borderColor);
                break;
            case 'B': // Bispo
                drawBishop(g2d, pieceColor, borderColor);
                break;
            case 'N': // Cavalo
                drawKnight(g2d, pieceColor, borderColor);
                break;
            case 'P': // Peão
                drawPawn(g2d, pieceColor, borderColor);
                break;
        }
        
        g2d.dispose();
        return new ImageIcon(image);
    }
    
    private static void drawKing(Graphics2D g2d, Color pieceColor, Color borderColor) {
        // Coroa com detalhes
        g2d.setColor(pieceColor);
        g2d.fillRoundRect(18, 15, 24, 10, 5, 5);
        g2d.fillRoundRect(16, 25, 28, 6, 3, 3);
        g2d.fillRoundRect(14, 31, 32, 6, 3, 3);
        
        // Pontas da coroa
        g2d.fillOval(16, 13, 6, 6);
        g2d.fillOval(38, 13, 6, 6);
        g2d.fillOval(27, 11, 6, 6);
        
        // Cruz da coroa
        g2d.setColor(borderColor);
        g2d.fillRoundRect(28, 8, 4, 10, 2, 2);
        g2d.fillRoundRect(26, 10, 8, 4, 2, 2);
        
        // Base elegante
        g2d.setColor(pieceColor);
        g2d.fillRoundRect(12, 37, 36, 10, 5, 5);
        g2d.fillRoundRect(18, 47, 24, 6, 3, 3);
        
        // Detalhes da base
        g2d.setColor(borderColor);
        g2d.drawRoundRect(12, 37, 36, 10, 5, 5);
        g2d.drawRoundRect(18, 47, 24, 6, 3, 3);
    }
    
    private static void drawQueen(Graphics2D g2d, Color pieceColor, Color borderColor) {
        // Coroa elegante com joias
        g2d.setColor(pieceColor);
        g2d.fillRoundRect(18, 15, 24, 12, 6, 6);
        g2d.fillRoundRect(16, 27, 28, 8, 4, 4);
        
        // Pontas da coroa com joias
        g2d.fillOval(14, 13, 8, 8);
        g2d.fillOval(38, 13, 8, 8);
        g2d.fillOval(26, 11, 8, 8);
        
        // Joias na coroa
        g2d.setColor(borderColor);
        g2d.fillOval(22, 18, 4, 4);
        g2d.fillOval(34, 18, 4, 4);
        g2d.fillOval(28, 22, 4, 4);
        
        // Base elegante
        g2d.setColor(pieceColor);
        g2d.fillRoundRect(12, 37, 36, 10, 5, 5);
        g2d.fillRoundRect(18, 47, 24, 6, 3, 3);
        
        // Detalhes da base
        g2d.setColor(borderColor);
        g2d.drawRoundRect(12, 37, 36, 10, 5, 5);
        g2d.drawRoundRect(18, 47, 24, 6, 3, 3);
    }
    
    private static void drawRook(Graphics2D g2d, Color pieceColor, Color borderColor) {
        // Torres da torre com detalhes
        g2d.setColor(pieceColor);
        g2d.fillRoundRect(16, 13, 8, 10, 3, 3);
        g2d.fillRoundRect(36, 13, 8, 10, 3, 3);
        g2d.fillRoundRect(24, 13, 12, 10, 3, 3);
        
        // Amelas nas torres
        g2d.setColor(borderColor);
        g2d.fillRoundRect(17, 15, 6, 2, 1, 1);
        g2d.fillRoundRect(37, 15, 6, 2, 1, 1);
        g2d.fillRoundRect(25, 15, 10, 2, 1, 1);
        g2d.fillRoundRect(17, 19, 6, 2, 1, 1);
        g2d.fillRoundRect(37, 19, 6, 2, 1, 1);
        g2d.fillRoundRect(25, 19, 10, 2, 1, 1);
        
        // Base elegante
        g2d.setColor(pieceColor);
        g2d.fillRoundRect(12, 37, 36, 10, 5, 5);
        g2d.fillRoundRect(18, 47, 24, 6, 3, 3);
        
        // Detalhes da base
        g2d.setColor(borderColor);
        g2d.drawRoundRect(12, 37, 36, 10, 5, 5);
        g2d.drawRoundRect(18, 47, 24, 6, 3, 3);
    }
    
    private static void drawBishop(Graphics2D g2d, Color pieceColor, Color borderColor) {
        // Mitra do bispo elegante
        g2d.setColor(pieceColor);
        int[] xPoints = {30, 18, 42};
        int[] yPoints = {12, 28, 28};
        g2d.fillPolygon(xPoints, yPoints, 3);
        
        // Cruz na mitra
        g2d.setColor(borderColor);
        g2d.fillRoundRect(28, 15, 4, 8, 2, 2);
        g2d.fillRoundRect(26, 17, 8, 4, 2, 2);
        
        // Fenda na mitra
        g2d.setColor(pieceColor);
        g2d.fillOval(28, 20, 4, 6);
        
        // Base elegante
        g2d.setColor(pieceColor);
        g2d.fillRoundRect(12, 37, 36, 10, 5, 5);
        g2d.fillRoundRect(18, 47, 24, 6, 3, 3);
        
        // Detalhes da base
        g2d.setColor(borderColor);
        g2d.drawRoundRect(12, 37, 36, 10, 5, 5);
        g2d.drawRoundRect(18, 47, 24, 6, 3, 3);
    }
    
    private static void drawKnight(Graphics2D g2d, Color pieceColor, Color borderColor) {
        // Cabeça do cavalo elegante
        g2d.setColor(pieceColor);
        g2d.fillOval(18, 12, 24, 18);
        
        // Orelha do cavalo
        g2d.fillOval(36, 8, 10, 10);
        
        // Olho do cavalo
        g2d.setColor(borderColor);
        g2d.fillOval(28, 18, 4, 4);
        
        // Nariz do cavalo
        g2d.setColor(pieceColor);
        g2d.fillOval(22, 20, 6, 4);
        
        // Crina do cavalo
        g2d.setColor(borderColor);
        g2d.fillOval(32, 14, 8, 6);
        
        // Base elegante
        g2d.setColor(pieceColor);
        g2d.fillRoundRect(12, 37, 36, 10, 5, 5);
        g2d.fillRoundRect(18, 47, 24, 6, 3, 3);
        
        // Detalhes da base
        g2d.setColor(borderColor);
        g2d.drawRoundRect(12, 37, 36, 10, 5, 5);
        g2d.drawRoundRect(18, 47, 24, 6, 3, 3);
    }
    
    private static void drawPawn(Graphics2D g2d, Color pieceColor, Color borderColor) {
        // Cabeça do peão elegante
        g2d.setColor(pieceColor);
        g2d.fillOval(20, 12, 20, 20);
        
        // Coroa do peão
        g2d.setColor(borderColor);
        g2d.fillRoundRect(24, 10, 12, 4, 2, 2);
        g2d.fillRoundRect(26, 8, 8, 4, 2, 2);
        
        // Base elegante
        g2d.setColor(pieceColor);
        g2d.fillRoundRect(12, 37, 36, 10, 5, 5);
        g2d.fillRoundRect(18, 47, 24, 6, 3, 3);
        
        // Detalhes da base
        g2d.setColor(borderColor);
        g2d.drawRoundRect(12, 37, 36, 10, 5, 5);
        g2d.drawRoundRect(18, 47, 24, 6, 3, 3);
    }
    
    /**
     * Retorna a imagem da peça baseada no tipo e cor
     */
    public static ImageIcon getPieceImage(String pieceType, boolean isWhite) {
        if (isWhite) {
            switch (pieceType) {
                case "King": return whiteKing;
                case "Queen": return whiteQueen;
                case "Rook": return whiteRook;
                case "Bishop": return whiteBishop;
                case "Knight": return whiteKnight;
                case "Pawn": return whitePawn;
                default: return whitePawn;
            }
        } else {
            switch (pieceType) {
                case "King": return blackKing;
                case "Queen": return blackQueen;
                case "Rook": return blackRook;
                case "Bishop": return blackBishop;
                case "Knight": return blackKnight;
                case "Pawn": return blackPawn;
                default: return blackPawn;
            }
        }
    }
    
    /**
     * Retorna o tamanho padrão das peças
     */
    public static int getPieceSize() {
        return PIECE_SIZE;
    }
}
