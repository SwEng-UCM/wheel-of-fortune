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
        setSize(1270, 1266); // Tamaño de la imagen de fondo
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            backgroundImage = ImageIO.read(new File("resources/EndBackground.jpg")); // Ruta de la imagen
        } catch (IOException e) {
            System.out.println("Error loading background image: " + e.getMessage());
        }

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // Panel con fondo oscuro para contraste
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(new Color(0, 0, 0, 220)); // Más opaco para mejor visibilidad

        // Fuente personalizada
        Font customFont = new Font("Arial Black", Font.BOLD, 50);

        // Cargar y escalar imágenes para los iconos
        ImageIcon trophyIcon = resizeIcon(new ImageIcon("resources/trophy.png"), 50, 50);
        ImageIcon moneyIcon = resizeIcon(new ImageIcon("resources/money.png"), 50, 50);

        // Crear etiquetas con iconos escalados
        JLabel winnerLabel = new JLabel(" Winner: " + winnerName, trophyIcon, JLabel.LEFT);
        winnerLabel.setFont(customFont);
        winnerLabel.setForeground(Color.YELLOW);
        winnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel moneyLabel = new JLabel(" Total earnings: $" + totalMoney, moneyIcon, JLabel.LEFT);
        moneyLabel.setFont(customFont);
        moneyLabel.setForeground(Color.GREEN);
        moneyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton exitButton = new JButton("EXIT");
        exitButton.setFont(new Font("Arial Black", Font.BOLD, 30));
        exitButton.setBackground(Color.RED);
        exitButton.setForeground(Color.WHITE);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3)); // Borde
        exitButton.setFocusPainted(false);
        exitButton.addActionListener(e -> System.exit(0));
        
     // Play Again Button
        JButton playAgainButton = new JButton("Play Again");
        playAgainButton.setFont(new Font("Arial Black", Font.BOLD, 30));
        playAgainButton.setBackground(Color.GREEN);
        playAgainButton.setForeground(Color.WHITE);
        playAgainButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        playAgainButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        playAgainButton.setFocusPainted(false);
        playAgainButton.addActionListener(e -> {
            game.restartGame();  // Restart the game when Play Again is clicked
            setVisible(false);    // Hide the EndScreen
            dispose();            // Dispose of the current EndScreen window
        });

        textPanel.add(Box.createVerticalStrut(50));
        textPanel.add(winnerLabel);
        textPanel.add(Box.createVerticalStrut(20));
        textPanel.add(moneyLabel);
        textPanel.add(Box.createVerticalStrut(40));
        textPanel.add(exitButton);
        textPanel.add(Box.createVerticalStrut(50));
        textPanel.add(playAgainButton); 
        
        textPanel.setPreferredSize(new Dimension(700, 500));  // Adjust as needed
        panel.setPreferredSize(new Dimension(1270, 1266));

        textPanel.setMaximumSize(new Dimension(700, 350)); // Ajustar el tamaño del panel de texto

        panel.add(Box.createVerticalStrut(200)); // Espaciado superior
        panel.add(textPanel);
        panel.add(Box.createVerticalStrut(100));

        setContentPane(panel);
    }

    // Método para redimensionar imágenes antes de agregarlas a JLabel
    private ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }

    
}
