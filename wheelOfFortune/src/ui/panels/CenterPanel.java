package ui.panels;

import ui.GameUI;
import players.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class CenterPanel extends JPanel {
    private GameUI gameUI;
    private JButton spinButton;
    private JButton guessButton;
    private JTextField letterInput;
    private JLabel currentPlayerLabel;

    // Panel para mostrar las carteras (wallets) de los jugadores
    private JPanel walletPanel;
    // Lista de etiquetas para actualizar el dinero de cada jugador
    private List<JLabel> budgetLabels;

    public CenterPanel(GameUI gameUI) {
        this.gameUI = gameUI;
        this.budgetLabels = new ArrayList<>();

        // Usamos un BoxLayout vertical para apilar el panel de acciones y el de carteras
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // ----- PANEL DE ACCIONES -----
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        spinButton = new JButton("Spin");
        spinButton.setFont(new Font("Arial", Font.BOLD, 16));
        spinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameUI.spinWheel();
                refreshButtons();
            }
        });

        guessButton = new JButton("Guess Letter");
        guessButton.setFont(new Font("Arial", Font.BOLD, 16));
        guessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String guessText = letterInput.getText().trim().toUpperCase();
                if (guessText.length() != 1) {
                    JOptionPane.showMessageDialog(CenterPanel.this, "Enter a single letter.");
                    return;
                }
                gameUI.guessLetter(guessText);
                letterInput.setText("");
                refreshButtons();
            }
        });

        letterInput = new JTextField(3);
        letterInput.setFont(new Font("Arial", Font.PLAIN, 16));

        currentPlayerLabel = new JLabel("Turn: Unknown");
        currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 16));

        actionPanel.add(spinButton);
        actionPanel.add(new JLabel("Letter:"));
        actionPanel.add(letterInput);
        actionPanel.add(guessButton);
        actionPanel.add(currentPlayerLabel);

        add(actionPanel);

        // ----- PANEL DE CARTERAS (WALLETS) -----
        initWalletPanel();

        refreshButtons();
    }

    /**
     * Inicializa el panel de carteras. Se crea un sub-panel para cada jugador con estilo moderno:
     * - Bordes redondeados y padding interno.
     * - Fondo en colores personalizados.
     */
    private void initWalletPanel() {
        List<Player> players = gameUI.getGame().getPlayers();
        // Layout en grid: 1 fila, tantos columnas como jugadores, con espacios amplios
        walletPanel = new JPanel(new GridLayout(1, players.size(), 20, 10));
        walletPanel.setBackground(Color.WHITE);
        walletPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Paleta de colores personalizada
        Color[] walletColors = {
            new Color(52, 152, 219),  // Azul
            new Color(231, 76, 60),   // Rojo
            new Color(46, 204, 113),  // Verde
            new Color(241, 196, 15),  // Amarillo
            new Color(155, 89, 182),  // Morado
            new Color(26, 188, 156)   // Turquesa
        };

        budgetLabels.clear();

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            JPanel playerPanel = new JPanel();
            playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
            // Borde redondeado con padding interno
            playerPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.DARK_GRAY, 2, true),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
            playerPanel.setBackground(Color.WHITE);

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

            budgetLabels.add(budgetLabel);

            playerPanel.add(nameLabel);
            playerPanel.add(Box.createVerticalStrut(8));
            playerPanel.add(budgetLabel);

            walletPanel.add(playerPanel);
        }

        add(walletPanel);
    }

    /**
     * Actualiza la etiqueta que muestra el jugador actual.
     */
    public void updateCurrentPlayer() {
        String currentPlayer = gameUI.getGame().getCurrentPlayerName();
        currentPlayerLabel.setText("Turn: " + currentPlayer);
    }

    /**
     * Actualiza las carteras mostrando el presupuesto actual de cada jugador.
     */
    public void updateWallets() {
        List<Player> players = gameUI.getGame().getPlayers();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            JLabel budgetLabel = budgetLabels.get(i);
            budgetLabel.setText("$" + player.getBudget());
        }
    }

    /**
     * Habilita o deshabilita los botones según el estado del juego.
     */
    private void refreshButtons() {
        boolean gameOver = gameUI.isGameOver();
        boolean hasSpun = gameUI.hasSpun();

        if (gameOver) {
            spinButton.setEnabled(false);
            guessButton.setEnabled(false);
        } else {
            spinButton.setEnabled(!hasSpun);
            guessButton.setEnabled(hasSpun);
        }
    }
}
