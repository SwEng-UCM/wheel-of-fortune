package ui;

import game.Game;
import players.Player;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class EndScreen extends JFrame {
    private BufferedImage backgroundImage;
    private ImageIcon winnerAvatar;

    /**
     * Constructor that displays the final end screen.
     *
     * @param winner      The winning player.
     * @param game        The Game instance.
     * @param finalPhrase The final winning phrase/word.
     */
    public EndScreen(Player winner, Game game, String finalPhrase) {
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

        // "VICTORY" label
        ShadowLabel victoryLabel = new ShadowLabel("VICTORY", 4, new Color(0, 0, 0, 180));
        victoryLabel.setFont(new Font("Arial Black", Font.BOLD, 65));
        victoryLabel.setForeground(Color.ORANGE);
        victoryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Final phrase label
        ShadowLabel phraseLabel = new ShadowLabel("Final phrase: " + finalPhrase, 3, new Color(0, 0, 0, 150));
        phraseLabel.setFont(new Font("Arial", Font.ITALIC | Font.BOLD, 32));
        phraseLabel.setForeground(new Color(218, 165, 32));
        phraseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Info panel for winner's avatar, name, and earnings
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(50, 50, 100, 220));
        infoPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        infoPanel.setMaximumSize(new Dimension(500, 500));

        JLabel avatarLabel = new JLabel(winnerAvatar);
        avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        ShadowLabel winnerLabel = new ShadowLabel(winner.getName(), 3, new Color(0, 0, 0, 150));
        winnerLabel.setFont(new Font("Arial Black", Font.BOLD, 35));
        winnerLabel.setForeground(Color.WHITE);
        winnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        ShadowLabel moneyLabel = new ShadowLabel("Earnings: $" + winner.getMoney(), 3, new Color(0, 0, 0, 150));
        moneyLabel.setFont(new Font("Arial", Font.BOLD, 32));
        moneyLabel.setForeground(Color.WHITE);
        moneyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        JButton playAgainButton = new JButton("PLAY AGAIN");
        playAgainButton.setFont(new Font("Arial Black", Font.BOLD, 28));
        playAgainButton.setBackground(new Color(52, 152, 219));
        playAgainButton.setForeground(Color.WHITE);
        playAgainButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        playAgainButton.setFocusPainted(false);
        playAgainButton.setPreferredSize(new Dimension(240, 70));
        playAgainButton.addActionListener(e -> {
            Game.getInstance(null).restartGame();
            dispose();
        });

        JButton exitButton = new JButton("EXIT");
        exitButton.setFont(new Font("Arial Black", Font.BOLD, 28));
        exitButton.setBackground(new Color(30, 110, 170));
        exitButton.setForeground(Color.WHITE);
        exitButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        exitButton.setFocusPainted(false);
        exitButton.setPreferredSize(new Dimension(180, 70));
        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(playAgainButton);
        buttonPanel.add(exitButton);

        infoPanel.add(Box.createVerticalStrut(25));
        infoPanel.add(avatarLabel);
        infoPanel.add(Box.createVerticalStrut(15));
        infoPanel.add(winnerLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(moneyLabel);
        infoPanel.add(Box.createVerticalStrut(30));
        infoPanel.add(buttonPanel);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.setPreferredSize(new Dimension(500, 500));

        panel.add(Box.createVerticalStrut(40));
        panel.add(victoryLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(phraseLabel);
        panel.add(Box.createVerticalStrut(30));
        panel.add(infoPanel);
        panel.add(Box.createVerticalStrut(50));

        setContentPane(panel);
        setVisible(true);
    }

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

