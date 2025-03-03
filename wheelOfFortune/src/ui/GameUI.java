package ui;

import game.Game;
import ui.panels.TopPanel;
import ui.panels.CenterPanel;
import ui.panels.BottomPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Clase principal de la interfaz: extiende JFrame y
 * coordina los paneles y la lógica del juego.
 */
public class GameUI extends JFrame {
    private Game game;

    // Variables de estado del juego
    private String selectedPhrase;  // La frase completa
    private char[] revealed;        // Estado de la frase con '_'
    private boolean hasSpun;        // Indica si el jugador ya giró en este turno
    private boolean gameOver;       // Indica si la frase se completó

    // Paneles
    private TopPanel topPanel;
    private CenterPanel centerPanel;
    private BottomPanel bottomPanel;

    public GameUI() {
        super("Wheel of Fortune Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setSize(800, 500);

        // 1. Inicializar la lógica y el estado del juego
        this.game = new Game();         // Carga frases, slices, etc.
        initGameState();                // Selecciona la frase y crea el array revealed

        // 2. Crear e incorporar los paneles
        topPanel = new TopPanel(this);
        add(topPanel, BorderLayout.NORTH);

        centerPanel = new CenterPanel(this);
        add(centerPanel, BorderLayout.CENTER);

        bottomPanel = new BottomPanel(this);
        add(bottomPanel, BorderLayout.SOUTH);

        updateUIState(); // Muestra el estado inicial (frase oculta, jugador actual)

        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Selecciona la frase y prepara el array 'revealed' con '_'.
     */
    private void initGameState() {
        selectedPhrase = game.getRandomPhrase();
        revealed = new char[selectedPhrase.length()];
        for (int i = 0; i < selectedPhrase.length(); i++) {
            revealed[i] = (selectedPhrase.charAt(i) == ' ') ? ' ' : '_';
        }
        hasSpun = false;
        gameOver = false;
    }

    // GETTERS para que los paneles accedan a la información si lo necesitan
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

    // SETTERS o métodos de actualización
    public void setHasSpun(boolean hasSpun) {
        this.hasSpun = hasSpun;
    }
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    /**
     * Actualiza el estado visual de la interfaz (frase, jugador actual).
     */
    public void updateUIState() {
        topPanel.updatePhraseLabel();
        centerPanel.updateCurrentPlayer();
    }

    /**
     * Comprueba si la frase está completamente descubierta.
     */
    public boolean isPhraseComplete() {
        for (char c : revealed) {
            if (c == '_') {
                return false;
            }
        }
        return true;
    }

    /**
     * Método para que un panel "gire la ruleta".
     */
    public void spinWheel() {
        if (!gameOver && !hasSpun) {
            try {
                String sliceResult = game.randomSlice();
                bottomPanel.appendMessage("🎡 Spin result: " + sliceResult);
                hasSpun = true;  // El jugador ya giró
            } catch (Exception ex) {
                bottomPanel.appendMessage("❌ Error spinning the wheel: " + ex.getMessage());
            }
        }
    }

    /**
     * Método para adivinar una letra. Devuelve true si fue correcta.
     */
    public boolean guessLetter(String guessText) {
        if (gameOver || !hasSpun) return false; // Si ya terminó o no ha girado, no hacemos nada

        char guessedLetter = guessText.charAt(0);
        boolean correctGuess = false;

        // Actualiza el panel según la letra
        for (int i = 0; i < selectedPhrase.length(); i++) {
            char originalChar = selectedPhrase.charAt(i);
            if (Character.toUpperCase(originalChar) == guessedLetter && revealed[i] == '_') {
                revealed[i] = originalChar;
                correctGuess = true;
            }
        }

        if (correctGuess) {
            bottomPanel.appendMessage("✔ Good! Letter '" + guessedLetter + "' is in the phrase.");
            updateUIState();

            if (isPhraseComplete()) {
                bottomPanel.appendMessage("🎉 Congratulations! The phrase is: " + selectedPhrase);
                gameOver = true;
            }
        } else {
            bottomPanel.appendMessage("✖ Letter '" + guessedLetter + "' is not in the phrase. Next player!");
            // Pasa al siguiente jugador
            game.nextTurn();
        }

        // Resetea turno si acierta o falla
        hasSpun = false;
        updateUIState();
        return correctGuess;
    }

    // Método main para probar directamente
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameUI::new);
    }
}
