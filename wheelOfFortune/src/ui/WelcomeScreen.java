package ui;

import javax.swing.*;

import network.GameClient;
import network.GameServer;

import java.awt.*;
import ui.GameUI;

public class WelcomeScreen extends JFrame {

    public WelcomeScreen() {
        setTitle("Wheel of Fortune");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. Load the original image
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/WelcomeScreen.png"));
        Image originalImage = originalIcon.getImage();

        // 2. Scale the image (keeping aspect ratio)
        int newWidth = 800;
        double aspectRatio = (double) originalIcon.getIconHeight() / originalIcon.getIconWidth();
        int newHeight = (int) (newWidth * aspectRatio);

        Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        // 3. Create a JLabel with the scaled image
        JLabel backgroundLabel = new JLabel(scaledIcon);
        backgroundLabel.setLayout(new BorderLayout());
        add(backgroundLabel, BorderLayout.CENTER);

        // 4. Create a panel for the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); // Make panel transparent

        // --- START GAME BUTTON ---
        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("SansSerif", Font.BOLD, 24));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setOpaque(false);
        startButton.setContentAreaFilled(false);
        startButton.setBorderPainted(false);

        // When clicked, close this screen and open the main GameUI
        startButton.addActionListener(e -> {
            dispose(); 

            new Thread(() -> main.Launcher.main(null)).start();
        });




        // --- EXIT BUTTON ---
        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("SansSerif", Font.BOLD, 24));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setOpaque(false);
        exitButton.setContentAreaFilled(false);
        exitButton.setBorderPainted(false);

        // When clicked, exit the application
        exitButton.addActionListener(e -> System.exit(0));

        // 5. Add both buttons to the panel
        buttonPanel.add(startButton);
        buttonPanel.add(exitButton);

        // 6. Place the button panel at the bottom of the background label
        backgroundLabel.add(buttonPanel, BorderLayout.SOUTH);

        // 7. Pack, center, and show
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Main method to test this screen
    public static void main(String[] args) {
        SwingUtilities.invokeLater(WelcomeScreen::new);
    }
}

