package ui;

import game.Game;
import players.Player; 

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class EndScreen extends JFrame {
    private BufferedImage backgroundImage;

    public EndScreen(String winnerName, int totalMoney, Game game) {
        setTitle("Game Over!");
        setSize(900, 700); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Cargar imagen de fondo
        try {
            backgroundImage = ImageIO.read(new File("resources/EndBackground.jpg"));
        } catch (IOException e) {
            System.out.println("Error loading background image: " + e.getMessage());
        }

        // Panel principal que pinta la imagen de fondo
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);

        // --- CONFIGURACIÓN DE COLORES ---
        Color overlayColor = new Color(44, 62, 80, 180);       // Gris semi-transparente
        Color exitButtonColor = new Color(30, 110, 170);        // Azul oscuro para EXIT (más oscuro)
        Color playAgainButtonColor = new Color(52, 152, 219);     // Azul más claro para Play Again
        Color buttonTextColor = Color.WHITE;

        // PANEL GRIS (overlay) que contendrá las etiquetas y los botones
        JPanel overlayPanel = new JPanel();
        overlayPanel.setLayout(new BoxLayout(overlayPanel, BoxLayout.Y_AXIS));
        overlayPanel.setBackground(overlayColor);
        overlayPanel.setPreferredSize(new Dimension(700, 350));
        overlayPanel.setMaximumSize(new Dimension(700, 350));

        // Fuente para las etiquetas
        Font labelFont = new Font("Arial", Font.BOLD, 50);

        // Iconos redimensionados
        ImageIcon trophyIcon = resizeIcon(new ImageIcon("resources/trophy.png"), 50, 50);
        ImageIcon moneyIcon  = resizeIcon(new ImageIcon("resources/money.png"), 50, 50);

        // Etiquetas con sombra (usando ShadowLabel)
        ShadowLabel winnerLabel = new ShadowLabel(" Winner: " + winnerName, trophyIcon, JLabel.LEFT);
        winnerLabel.setFont(labelFont);
        winnerLabel.setForeground(Color.WHITE);
        winnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        ShadowLabel moneyLabel = new ShadowLabel(" Total earnings: $" + totalMoney, moneyIcon, JLabel.LEFT);
        moneyLabel.setFont(labelFont);
        moneyLabel.setForeground(Color.WHITE);
        moneyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Botón EXIT (más pequeño: 140×40 píxeles)
        JButton exitButton = new JButton("EXIT");
        exitButton.setFont(new Font("Arial Black", Font.BOLD, 30));
        exitButton.setBackground(exitButtonColor);
        exitButton.setForeground(buttonTextColor);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        exitButton.setFocusPainted(false);
        exitButton.addActionListener(e -> System.exit(0));
        exitButton.setPreferredSize(new Dimension(140, 40));
        exitButton.setMaximumSize(new Dimension(140, 40));

        // Botón PLAY AGAIN (dentro del overlay) - 220×45 píxeles
        JButton playAgainButton = new JButton("Play Again");
        playAgainButton.setFont(new Font("Arial Black", Font.BOLD, 30));
        playAgainButton.setBackground(playAgainButtonColor);
        playAgainButton.setForeground(buttonTextColor);
        playAgainButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        playAgainButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        playAgainButton.setFocusPainted(false);
        playAgainButton.addActionListener(e -> {
            Game.getInstance(null).restartGame();
            dispose();
        });
        playAgainButton.setPreferredSize(new Dimension(220, 45));
        playAgainButton.setMaximumSize(new Dimension(220, 45));

        // Agregar componentes al overlay con espaciado
        overlayPanel.add(Box.createVerticalStrut(20));
        overlayPanel.add(winnerLabel);
        overlayPanel.add(Box.createVerticalStrut(20));
        overlayPanel.add(moneyLabel);
        overlayPanel.add(Box.createVerticalStrut(30));
        overlayPanel.add(exitButton);
        overlayPanel.add(Box.createVerticalStrut(20));
        overlayPanel.add(playAgainButton);
        overlayPanel.add(Box.createVerticalStrut(20));

        // Posicionar el overlayPanel dentro del mainPanel
        // Se ha reducido el margen superior a 130 para moverlo un poco más arriba
        mainPanel.add(Box.createVerticalStrut(130));  
        mainPanel.add(overlayPanel);
        mainPanel.add(Box.createVerticalStrut(50));   // Margen inferior

        setContentPane(mainPanel);
    }

    // Método auxiliar para redimensionar iconos
    private ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }

    // Clase interna para etiquetas con sombra
    private static class ShadowLabel extends JLabel {
        private Color shadowColor = new Color(0, 0, 0, 150); // Sombra semitransparente
        private int shadowOffset = 2;

        public ShadowLabel(String text, Icon icon, int horizontalAlignment) {
            super(text, icon, horizontalAlignment);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setFont(getFont());
            String text = getText();
            if (text != null && !text.isEmpty()) {
                FontMetrics fm = g2d.getFontMetrics();
                int x = 0;
                int y = fm.getAscent();
                Icon icon = getIcon();
                int gap = getIconTextGap();
                if (icon != null) {
                    x = icon.getIconWidth() + gap;
                }
                g2d.setColor(shadowColor);
                g2d.drawString(text, x + shadowOffset, y + shadowOffset);
            }
            g2d.dispose();
            super.paintComponent(g);
        }
    }
}
