package ui;

import game.Game;
import players.Player;
import ui.panels.TopPanel;
import ui.panels.CenterPanel;
import ui.panels.BottomPanel;

import javax.swing.*;
import java.awt.*;

public class GameUI extends JFrame {
    private Game game;

    // Variables de estado del juego
    private String selectedPhrase;  // La frase completa
    private char[] revealed;        // Estado de la frase con '_'
    private boolean hasSpun;        // Indica si el jugador ya giró en este turno
    private boolean gameOver;       // Indica si la frase se completó
    private int currentSpinValue;   // Almacena el valor del giro actual

    // Paneles
    private TopPanel topPanel;
    private CenterPanel centerPanel;
    private BottomPanel bottomPanel;

    public GameUI() {
        super("Wheel of Fortune Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setSize(800, 500);

        this.game = new Game();
        registerPlayers();
        initGameState();

        // Crear e incorporar los paneles
        topPanel = new TopPanel(this);
        add(topPanel, BorderLayout.NORTH);

        centerPanel = new CenterPanel(this);
        add(centerPanel, BorderLayout.CENTER);

        bottomPanel = new BottomPanel(this);
        add(bottomPanel, BorderLayout.SOUTH);

        updateUIState(); // Actualiza la frase, el turno y las carteras

        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Selecciona la frase y prepara el array 'revealed' con '_' para cada carácter (excepto espacios).
     */
    private void initGameState() {
        selectedPhrase = game.getRandomPhrase();
        revealed = new char[selectedPhrase.length()];
        for (int i = 0; i < selectedPhrase.length(); i++) {
            revealed[i] = (selectedPhrase.charAt(i) == ' ') ? ' ' : '_';
        }
        hasSpun = false;
        gameOver = false;
        currentSpinValue = 0;
    }

    // GETTERS para que los paneles accedan a la información
    public Game getGame() {
        return game;
    }
    public String getSelectedPhrase() {
        return selectedPhrase;
    }
    public char[] getRevealed() {
        return revealed;
    }
    public boolean hasSpun() {
        return hasSpun;
    }
    public boolean isGameOver() {
        return gameOver;
    }
    public void setHasSpun(boolean hasSpun) {
        this.hasSpun = hasSpun;
    }
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    /**
     * Actualiza la interfaz: la frase, el jugador actual y las carteras.
     */
    public void updateUIState() {
        topPanel.updatePhraseLabel();
        centerPanel.updateCurrentPlayer();
        centerPanel.updateWallets(); // Refresca la visualización de las carteras
    }

    /**
     * Gira la ruleta y almacena el valor obtenido. Se muestra únicamente el resultado textual.
     */
    public void spinWheel() {
        if (!gameOver && !hasSpun) {
            try {
                String sliceResult = game.randomSlice();
                currentSpinValue = game.getSliceValue(sliceResult);
                bottomPanel.appendMessage("🎡 Spin result: " + sliceResult);
                hasSpun = true;
            } catch (Exception ex) {
                bottomPanel.appendMessage("❌ Error spinning the wheel: " + ex.getMessage());
            }
        }
    }

    /**
     * Procesa la adivinanza de una letra:
     * - Recorre la frase y cuenta las ocurrencias de la letra adivinada.
     * - Si hay aciertos, multiplica el valor del giro por las ocurrencias y se suma al dinero del jugador actual.
     * - Se actualiza la UI, incluidas las carteras.
     */
    public boolean guessLetter(String guessText) {
        if (gameOver || !hasSpun) return false;

        char guessedLetter = guessText.charAt(0);
        int occurrences = 0;
        for (int i = 0; i < selectedPhrase.length(); i++) {
            char originalChar = selectedPhrase.charAt(i);
            if (Character.toUpperCase(originalChar) == guessedLetter && revealed[i] == '_') {
                revealed[i] = originalChar;
                occurrences++;
            }
        }

        if (occurrences > 0) {
            Player currentPlayer = game.getPlayers().get(game.getCurrentPlayerIndex());
            int amountWon = currentSpinValue * occurrences;
            currentPlayer.addMoney(amountWon);
            bottomPanel.appendMessage("✔ Good! Letter '" + guessedLetter + "' is in the phrase (" 
                + occurrences + " occurrence" + (occurrences > 1 ? "s" : "") + "). " 
                + currentPlayer.getName() + " wins $" + amountWon 
                + "! Total: $" + currentPlayer.getMoney());
            updateUIState();
            if (isPhraseComplete()) {
                bottomPanel.appendMessage("🎉 Congratulations! The phrase is: " + selectedPhrase);
                gameOver = true;
            }
        } else {
            bottomPanel.appendMessage("✖ Letter '" + guessedLetter + "' is not in the phrase. Next player!");
            game.nextTurn();
            updateUIState();
        }

        hasSpun = false;
        updateUIState();
        return occurrences > 0;
    }

    /**
     * Comprueba si la frase está completamente descubierta.
     */
    public boolean isPhraseComplete() {
        for (char c : revealed) {
            if (c == '_') return false;
        }
        return true;
    }

    /**
     * Pide el número de jugadores (mínimo 2) y los registra.
     */
    private void registerPlayers() {
        int numPlayers = -1;
        while (numPlayers < 2) {
            String input = JOptionPane.showInputDialog(this, "Enter the number of players (minimum 2):");
            try {
                numPlayers = Integer.parseInt(input);
                if (numPlayers < 2) {
                    JOptionPane.showMessageDialog(this, "You need at least 2 players!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid number. Try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        for (int i = 0; i < numPlayers; i++) {
            String playerName;
            do {
                playerName = JOptionPane.showInputDialog(this, "Enter name for Player " + (i + 1) + ":");
            } while (playerName == null || playerName.trim().isEmpty());
            game.addPlayer(new Player(playerName));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameUI::new);
    }
}

