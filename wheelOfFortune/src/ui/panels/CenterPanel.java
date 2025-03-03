package ui.panels;

import ui.GameUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Panel central: botones para "Spin" y "Guess Letter", campo de texto,
 * y etiqueta con el turno actual.
 */
public class CenterPanel extends JPanel {
    private GameUI gameUI;
    private JButton spinButton;
    private JButton guessButton;
    private JTextField letterInput;
    private JLabel currentPlayerLabel;

    public CenterPanel(GameUI gameUI) {
        this.gameUI = gameUI;

        setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));

        spinButton = new JButton("Spin");
        spinButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        spinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Lógica de girar la ruleta
                gameUI.spinWheel();
                refreshButtons();
            }
        });

        guessButton = new JButton("Guess Letter");
        guessButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        guessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String guessText = letterInput.getText().trim().toUpperCase();
                if (guessText.length() != 1) {
                    JOptionPane.showMessageDialog(CenterPanel.this, "Enter a single letter.");
                    return;
                }
                boolean correct = gameUI.guessLetter(guessText);
                letterInput.setText("");
                refreshButtons();
            }
        });

        letterInput = new JTextField(3);
        letterInput.setFont(new Font("SansSerif", Font.PLAIN, 16));

        currentPlayerLabel = new JLabel("Turn: Unknown");
        currentPlayerLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        add(spinButton);
        add(new JLabel("Letter:"));
        add(letterInput);
        add(guessButton);
        add(currentPlayerLabel);

        refreshButtons();
    }

    /**
     * Actualiza la etiqueta del jugador actual y el estado de los botones.
     */
    public void updateCurrentPlayer() {
        String currentPlayer = gameUI.getGame().getCurrentPlayerName();
        currentPlayerLabel.setText("Turn: " + currentPlayer);
    }


    /**
     * Habilita o deshabilita botones según el estado del juego.
     */
    private void refreshButtons() {
        boolean gameOver = gameUI.isGameOver();
        boolean hasSpun = gameUI.hasSpun();

        // Si el juego terminó, deshabilitamos ambos
        if (gameOver) {
            spinButton.setEnabled(false);
            guessButton.setEnabled(false);
        } else {
            // Si aún no ha girado, puede girar pero no adivinar
            spinButton.setEnabled(!hasSpun);
            guessButton.setEnabled(hasSpun);
        }
    }
}
