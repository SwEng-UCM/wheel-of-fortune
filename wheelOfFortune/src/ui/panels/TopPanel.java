package ui.panels;

import ui.GameUI;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import game.Game;
import network.ChatMessage;
import network.GameClient;

public class TopPanel extends JPanel {
    private static final int BANNER_WIDTH = 400;
    private static final int BANNER_HEIGHT = 80;

    private GameUI gameUI;
    private JLabel phraseLabel;
    private JDialog chatDialog;
    private JTextArea chatArea;

    public TopPanel(GameUI gameUI) {
        this.gameUI = gameUI;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setOpaque(true);

        // --- Banner ---
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/TopPanel.png"));
        Image originalImage = originalIcon.getImage();
        Image scaledImage = originalImage.getScaledInstance(BANNER_WIDTH, BANNER_HEIGHT, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JLabel bannerLabel = new JLabel(scaledIcon);
        bannerLabel.setPreferredSize(new Dimension(BANNER_WIDTH, BANNER_HEIGHT));
        bannerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bannerLabel.setBackground(Color.WHITE);
        bannerLabel.setOpaque(true);

        // --- Phrase Panel ---
        JPanel phrasePanel = new JPanel(new BorderLayout());
        phrasePanel.setBackground(Color.WHITE);
        phraseLabel = new JLabel("", SwingConstants.CENTER);
        phraseLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        phraseLabel.setForeground(Color.BLACK);
        phraseLabel.setOpaque(false);
        phrasePanel.add(phraseLabel, BorderLayout.CENTER);

        // --- Button Panel (derecha) ---
        JButton saveButton = new JButton("💾 Save");
        JButton loadButton = new JButton("📂 Load");
        JButton exitButton = new JButton("❌ Exit");
        JButton chatButton = new JButton("💬 Host");
        JButton syncButton = new JButton("📡 Sync");

        saveButton.addActionListener(e -> {
            String saveName = JOptionPane.showInputDialog(this, "Enter a name for the saved game:");
            if (saveName != null && !saveName.trim().isEmpty()) {
                gameUI.getGame().saveGameState(saveName.trim());
            }
        });

        loadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser("saved_games");
            fileChooser.setDialogTitle("Select a saved game to load");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String filename = selectedFile.getName();
                if (filename.endsWith(".json")) {
                    String saveName = filename.substring(0, filename.length() - 5);
                    gameUI.getGame().loadGameState(saveName, gameUI);
                    gameUI.updateUIState();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid file. Please select a .json saved game file.");
                }
            }
        });

        exitButton.addActionListener(e -> System.exit(0));
        chatButton.addActionListener(e -> showChatDialog());
        syncButton.addActionListener(e -> {
            if (GameUI.serverInstance != null) {
                GameUI.serverInstance.broadcastGameState(gameUI.getGame());
                int count = GameUI.serverInstance.getClientCount();
                gameUI.setConnectedClients(count);
                gameUI.updateStatusLabel();
                JOptionPane.showMessageDialog(this, "✅ Estado reenviado a todos los clientes.");
            } else {
                JOptionPane.showMessageDialog(this, "❌ No hay servidor activo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(syncButton);
        buttonPanel.add(chatButton);
        buttonPanel.add(exitButton);

        // --- Top bar: settings + botones ---
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.add(Box.createHorizontalStrut(10), BorderLayout.WEST); // espacio vacío
        topBar.add(buttonPanel, BorderLayout.EAST);

        // --- Status label ---
        JLabel connectionLabel = new JLabel("Status...");
        connectionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        connectionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        connectionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 10));
        gameUI.setStatusLabel(connectionLabel);

        JPanel connectionPanel = new JPanel(new BorderLayout());
        connectionPanel.setBackground(Color.WHITE);
        connectionPanel.add(connectionLabel, BorderLayout.EAST);

        // --- Agrupar cabecera completa ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.add(topBar, BorderLayout.NORTH);
        header.add(bannerLabel, BorderLayout.CENTER);
        header.add(connectionPanel, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);
        add(phrasePanel, BorderLayout.CENTER);

        updatePhraseLabel();
    }

    public void updatePhraseLabel() {
        char[] revealed = gameUI.getRevealed();
        if (revealed == null) {
            phraseLabel.setText("Waiting for phrase...");
            return;
        }
        StringBuilder sb = new StringBuilder("<html><body style='width:600px; text-align:center; font-family:monospace;'>");
        for (char c : revealed) {
            if (c == ' ') sb.append("&nbsp; ");
            else sb.append(c).append(' ');
        }
        sb.append("</body></html>");
        phraseLabel.setText(sb.toString());
    }

    private void showChatDialog() {
        if (chatDialog != null && chatDialog.isVisible()) {
            chatDialog.toFront();
            return;
        }

        chatDialog = new JDialog(gameUI, "Game Chat", false);
        chatDialog.setSize(400, 300);
        chatDialog.setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        chatDialog.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        JTextField messageField = new JTextField();
        JButton sendButton = new JButton("Send");

        sendButton.addActionListener(e -> {
            String message = messageField.getText().trim();
            if (!message.isEmpty()) {
                if (GameUI.serverInstance != null) {
                    ChatMessage chatMsg = new ChatMessage(message, true);
                    GameUI.serverInstance.broadcastChatMessage(chatMsg);
                    appendToChat(chatMsg);
                } else {
                    ((GameClient) gameUI.getClient()).sendChatMessage(message);
                }
                messageField.setText("");
            }
        });

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        chatDialog.add(inputPanel, BorderLayout.SOUTH);

        chatDialog.setLocationRelativeTo(gameUI);
        chatDialog.setVisible(true);
    }

    public void appendToChat(ChatMessage message) {
        if (chatArea != null) {
            chatArea.setForeground(message.getColor());
            chatArea.append(message.toString() + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        }
    }
}

