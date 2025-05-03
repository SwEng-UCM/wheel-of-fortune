package ui.panels;

import ui.GameUI;
import javax.swing.*;
import java.awt.*;
import java.io.File;

import game.Game;

public class TopPanel extends JPanel {
    private static final int BANNER_WIDTH = 400;
    private static final int BANNER_HEIGHT = 80;

    private GameUI gameUI;
    private JLabel phraseLabel;

    public TopPanel(GameUI gameUI) {
        this.gameUI = gameUI;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setOpaque(true);

        // Banner
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/TopPanel.png"));
        Image originalImage = originalIcon.getImage();
        Image scaledImage = originalImage.getScaledInstance(BANNER_WIDTH, BANNER_HEIGHT, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JLabel bannerLabel = new JLabel(scaledIcon);
        bannerLabel.setPreferredSize(new Dimension(BANNER_WIDTH, BANNER_HEIGHT));
        bannerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bannerLabel.setBackground(Color.WHITE);
        bannerLabel.setOpaque(true);
        add(bannerLabel, BorderLayout.NORTH);

        // Phrase panel
        JPanel phrasePanel = new JPanel(new BorderLayout());
        phrasePanel.setBackground(Color.WHITE);
        phrasePanel.setOpaque(true);

        phraseLabel = new JLabel("", SwingConstants.CENTER);
        phraseLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        phraseLabel.setForeground(Color.BLACK);
        phraseLabel.setOpaque(false);

        phrasePanel.add(phraseLabel, BorderLayout.CENTER);
        add(phrasePanel, BorderLayout.CENTER);

        // Buttons panel (Save, Load, Exit)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton saveButton = new JButton("💾 Save");
        JButton loadButton = new JButton("📂 Load");
        JButton exitButton = new JButton("❌ Exit");
        
        
        JButton syncButton = new JButton("📡 Sync");
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

        
        // Save action
        saveButton.addActionListener(e -> {
            String saveName = JOptionPane.showInputDialog(this, "Enter a name for the saved game:");
            if (saveName != null && !saveName.trim().isEmpty()) {
                gameUI.getGame().saveGameState(saveName.trim());
            }
        });

        // Load action
        loadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser("saved_games");
            fileChooser.setDialogTitle("Select a saved game to load");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String filename = selectedFile.getName();

                if (filename.endsWith(".json")) {
                    String saveName = filename.substring(0, filename.length() - 5); // remove ".json"
                    gameUI.getGame().loadGameState(saveName, gameUI);
                    gameUI.updateUIState();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid file. Please select a .json saved game file.");
                }
            }
        });

        // Exit action
        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(exitButton);
        buttonPanel.add(syncButton); 


        // Status label (host/client info)
        JPanel connectionPanel = new JPanel(new BorderLayout());
        connectionPanel.setBackground(Color.WHITE);

        JLabel connectionLabel = new JLabel("Status...");
        connectionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        connectionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        connectionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 10));
        connectionPanel.add(connectionLabel, BorderLayout.EAST);

        // Registrar el label en GameUI para que pueda actualizarlo dinámicamente
        gameUI.setStatusLabel(connectionLabel);

        // Contenedor que incluye botones arriba y estado debajo
        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setBackground(Color.WHITE);
        bottomContainer.add(buttonPanel, BorderLayout.CENTER);
        bottomContainer.add(connectionPanel, BorderLayout.SOUTH);

        // Añadir al sur del panel principal
        add(bottomContainer, BorderLayout.SOUTH);

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
            if (c == ' ') {
                sb.append("&nbsp; ");
            } else {
                sb.append(c).append(' ');
            }
        }
        sb.append("</body></html>");
        phraseLabel.setText(sb.toString());
    }

    
    
}
