package ui.panels;

import ui.GameUI;
import game.GuessLetterCommand;
import players.Player;
import game.BuyVowelCommand;


import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class CenterPanel extends JPanel {
    private GameUI gameUI;
    private JButton spinButton;
    private JButton guessButton;
    private JButton undoButton;
    private JTextField letterInput;
    private JLabel currentPlayerLabel;
    private JButton buyVowelButton;
    private JButton solveButton;

    private JPanel walletPanel;
    private List<JLabel> budgetLabels;

    public CenterPanel(GameUI gameUI) {
        this.gameUI = gameUI;
        this.budgetLabels = new ArrayList<>();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        solveButton = new JButton("SOLVE");
        solveButton.setFont(new Font("Arial", Font.BOLD, 16));
        solveButton.setBackground(Color.ORANGE);
        solveButton.setForeground(Color.BLACK);
        solveButton.setFocusPainted(false);
        solveButton.addActionListener(e -> {
            String playerSolution = JOptionPane.showInputDialog(this, "Enter the full phrase:");
            if (playerSolution != null && !playerSolution.trim().isEmpty()) {
                gameUI.attemptSolve(playerSolution.trim());
            }
        });
        actionPanel.add(solveButton);

        spinButton = new JButton("Spin");
        spinButton.setFont(new Font("Arial", Font.BOLD, 16));
        spinButton.addActionListener(e -> {
            gameUI.spinWheel();
            refreshButtons();
        });
        actionPanel.add(spinButton);

        actionPanel.add(new JLabel("Letter:"));

        letterInput = new JTextField(3);
        letterInput.setFont(new Font("Arial", Font.PLAIN, 16));
        actionPanel.add(letterInput);

        guessButton = new JButton("Guess Letter");
        guessButton.setFont(new Font("Arial", Font.BOLD, 16));
        guessButton.addActionListener(e -> {
            String guessText = letterInput.getText().trim().toUpperCase();
            if (guessText.length() != 1) {
                JOptionPane.showMessageDialog(CenterPanel.this, "Enter a single letter.");
                return;
            }
            char guessedChar = guessText.charAt(0);
            GuessLetterCommand command = new GuessLetterCommand(gameUI, guessedChar);
            gameUI.getCommandManager().executeCommand(command);
            letterInput.setText("");
            refreshButtons();
        });
        actionPanel.add(guessButton);

        buyVowelButton = new JButton("Buy Vowel ($75)");
        buyVowelButton.setFont(new Font("Arial", Font.BOLD, 16));
        
        
        buyVowelButton.addActionListener(e -> {
            String vowelText = letterInput.getText().trim().toUpperCase();
            if (vowelText.length() != 1 || !"AEIOU".contains(vowelText)) {
                JOptionPane.showMessageDialog(CenterPanel.this, "You can only buy vowels (A, E, I, O, U).");
                return;
            }
            char vowel = vowelText.charAt(0);
            BuyVowelCommand command = new BuyVowelCommand(gameUI, vowel);
            gameUI.getCommandManager().executeCommand(command);
            letterInput.setText("");
            refreshButtons();
        });
        
        
        
        actionPanel.add(buyVowelButton);

        undoButton = new JButton("Undo");
        undoButton.setFont(new Font("Arial", Font.BOLD, 16));
        undoButton.addActionListener(e -> gameUI.getCommandManager().undo());
        actionPanel.add(undoButton);

        currentPlayerLabel = new JLabel("Turn: Unknown");
        currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        actionPanel.add(currentPlayerLabel);

        add(actionPanel);

        initWalletPanel();
        refreshButtons();
    }

    private void initWalletPanel() {
        List<Player> players = gameUI.getGame().getPlayers();
        walletPanel = new JPanel(new GridLayout(1, players.size(), 20, 10));
        walletPanel.setBackground(Color.WHITE);
        walletPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Color[] walletColors = {
            new Color(52, 152, 219),
            new Color(231, 76, 60),
            new Color(46, 204, 113),
            new Color(241, 196, 15),
            new Color(155, 89, 182),
            new Color(26, 188, 156)
        };

        budgetLabels.clear();

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            JPanel playerPanel = new JPanel();
            playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
            playerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 2, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
            playerPanel.setBackground(Color.WHITE);

            JPanel splitPanel = new JPanel();
            splitPanel.setLayout(new BoxLayout(splitPanel, BoxLayout.X_AXIS));
            splitPanel.setBackground(Color.WHITE);

            JPanel avatarPanel = new JPanel(new BorderLayout());
            avatarPanel.setBackground(Color.WHITE);
            JLabel avatarLabel = new JLabel();
            avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
            if (player.getAvatar() != null) {
                Image image = player.getAvatar().getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                avatarLabel.setIcon(new ImageIcon(image));
            } else {
                avatarLabel.setText("No Avatar");
            }
            avatarPanel.add(avatarLabel, BorderLayout.CENTER);
            avatarPanel.setPreferredSize(new Dimension(80, 60));

            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setBackground(Color.WHITE);
            JLabel nameLabel = new JLabel(player.getName(), SwingConstants.CENTER);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel budgetLabel = new JLabel("$" + player.getBudget(), SwingConstants.CENTER);
            budgetLabel.setFont(new Font("Arial", Font.BOLD, 16));
            budgetLabel.setOpaque(true);
            budgetLabel.setBackground(walletColors[i % walletColors.length]);
            budgetLabel.setForeground(Color.WHITE);
            budgetLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            budgetLabel.setPreferredSize(new Dimension(120, 40));
            budgetLabel.setMaximumSize(new Dimension(120, 40));
            infoPanel.add(nameLabel);
            infoPanel.add(Box.createVerticalStrut(8));
            infoPanel.add(budgetLabel);

            splitPanel.add(avatarPanel);
            splitPanel.add(Box.createHorizontalStrut(10));
            splitPanel.add(infoPanel);

            playerPanel.add(splitPanel);
            walletPanel.add(playerPanel);
            budgetLabels.add(budgetLabel);
        }

        add(walletPanel);
    }

    public void updateCurrentPlayer() {
        String currentPlayer = gameUI.getGame().getCurrentPlayerName();
        currentPlayerLabel.setText("Turn: " + currentPlayer);
    }

    public void updateWallets() {
        List<Player> players = gameUI.getGame().getPlayers();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            JLabel budgetLabel = budgetLabels.get(i);
            budgetLabel.setText("$" + player.getBudget());
        }
    }

    private void refreshButtons() {
        boolean gameOver = gameUI.isGameOver();
        boolean hasSpun = gameUI.hasSpun();

        spinButton.setEnabled(!gameOver && !hasSpun);
        guessButton.setEnabled(!gameOver && hasSpun);
    }
}
