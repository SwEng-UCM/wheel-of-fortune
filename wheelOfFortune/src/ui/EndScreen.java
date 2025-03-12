package ui;

import javax.swing.*;
import java.awt.*;

public class EndScreen extends JFrame {
    public EndScreen(String winnerName, int totalMoney) {
        setTitle("Game Over!");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(30, 144, 255)); // Dynamic blue background
        
        JLabel titleLabel = new JLabel("🎉 Game Over! 🎉");
        titleLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel winnerLabel = new JLabel("🏆 Winner: " + winnerName);
        winnerLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        winnerLabel.setForeground(Color.YELLOW);
        winnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel moneyLabel = new JLabel("💰 Total earnings: $" + totalMoney);
        moneyLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        moneyLabel.setForeground(Color.GREEN);
        moneyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.BOLD, 16));
        exitButton.setBackground(Color.RED);
        exitButton.setForeground(Color.WHITE);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.addActionListener(e -> System.exit(0));
        
        panel.add(Box.createVerticalStrut(20));
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(winnerLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(moneyLabel);
        panel.add(Box.createVerticalStrut(30));
        panel.add(exitButton);
        
        add(panel);
    }
}