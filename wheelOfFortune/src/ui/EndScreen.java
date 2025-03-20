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
    private ImageIcon winnerAvatar;

    public EndScreen(Player winner, Game game) {
        setTitle("Victory!");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            backgroundImage = ImageIO.read(new File("resources/EndBackground.jpg"));
        } catch (IOException e) {
            System.out.println("Error loading background image: " + e.getMessage());
        }

        this.winnerAvatar = new ImageIcon(
            winner.getAvatar().getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH)
        );

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
                g.setColor(new Color(0, 0, 0, 180));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // Banner Victory con sombra mejorada
        ShadowLabel victoryLabel = new ShadowLabel("VICTORY", 4, new Color(0, 0, 0, 180));
        victoryLabel.setFont(new Font("Arial Black", Font.BOLD, 65));
        victoryLabel.setForeground(Color.ORANGE);
        victoryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Panel central más espacioso
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(50, 50, 100, 220));
        infoPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        infoPanel.setMaximumSize(new Dimension(500, 500)); // Más grande para mejor organización

        JLabel avatarLabel = new JLabel(winnerAvatar);
        avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Nombre del ganador con sombra
        ShadowLabel winnerLabel = new ShadowLabel(winner.getName(), 3, new Color(0, 0, 0, 150));
        winnerLabel.setFont(new Font("Arial Black", Font.BOLD, 35));
        winnerLabel.setForeground(Color.WHITE);
        winnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Earnings con sombra
        ShadowLabel moneyLabel = new ShadowLabel("Earnings: $" + winner.getMoney(), 3, new Color(0, 0, 0, 150));
        moneyLabel.setFont(new Font("Arial", Font.BOLD, 32));
        moneyLabel.setForeground(Color.WHITE);
        moneyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        // Botón Play Again
        JButton playAgainButton = new JButton("PLAY AGAIN");
        playAgainButton.setFont(new Font("Arial Black", Font.BOLD, 28));
        playAgainButton.setBackground(new Color(52, 152, 219));
        playAgainButton.setForeground(Color.WHITE);
        playAgainButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        playAgainButton.setFocusPainted(false);
        playAgainButton.setPreferredSize(new Dimension(240, 70)); // Más grande
        playAgainButton.addActionListener(e -> {
            Game.getInstance(null).restartGame();
            dispose();
        });

        // Botón Exit
        JButton exitButton = new JButton("EXIT");
        exitButton.setFont(new Font("Arial Black", Font.BOLD, 28));
        exitButton.setBackground(new Color(30, 110, 170));
        exitButton.setForeground(Color.WHITE);
        exitButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        exitButton.setFocusPainted(false);
        exitButton.setPreferredSize(new Dimension(180, 70)); // Más grande
        exitButton.addActionListener(e -> System.exit(0));

        // Asegurar que los botones estén bien alineados y con espacio
        buttonPanel.add(playAgainButton);
        buttonPanel.add(exitButton);

        // Agregar espacio entre los elementos para mejor alineación
        infoPanel.add(Box.createVerticalStrut(25)); // Espaciado superior
        infoPanel.add(avatarLabel);
        infoPanel.add(Box.createVerticalStrut(15)); // Espaciado entre avatar y nombre
        infoPanel.add(winnerLabel);
        infoPanel.add(Box.createVerticalStrut(10)); // Espaciado entre nombre y earnings
        infoPanel.add(moneyLabel);
        infoPanel.add(Box.createVerticalStrut(30)); // Más espacio antes de los botones
        infoPanel.add(buttonPanel);
        infoPanel.add(Box.createVerticalStrut(20)); // Espaciado inferior

        infoPanel.setPreferredSize(new Dimension(500, 500));

        // Ajustar el posicionamiento general
        panel.add(Box.createVerticalStrut(40)); // Espacio superior antes del título
        panel.add(victoryLabel);
        panel.add(Box.createVerticalStrut(30)); // Espacio entre "VICTORY" y el cuadro de información
        panel.add(infoPanel);
        panel.add(Box.createVerticalStrut(50)); // Espacio inferior para centrar mejor

        setContentPane(panel);
    }

    // Clase interna para etiquetas con sombra mejorada
    private static class ShadowLabel extends JLabel {
        private final int shadowOffset;
        private final Color shadowColor;

        public ShadowLabel(String text, int shadowOffset, Color shadowColor) {
            super(text);
            this.shadowOffset = shadowOffset;
            this.shadowColor = shadowColor;
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
                g2d.setColor(shadowColor);
                g2d.drawString(text, x + shadowOffset, y + shadowOffset);
            }
            g2d.dispose();
            super.paintComponent(g);
        }
    }
}
