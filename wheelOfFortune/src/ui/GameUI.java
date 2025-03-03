package ui;

import game.Game;
import players.Player;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class GameUI extends JFrame {
    private Game game;
    private JLabel phraseLabel;
    private JLabel currentPlayerLabel;
    private JTextField letterInput;
    private JButton spinButton;
    private JButton guessButton;
    private JTextArea messageArea;

    // Variables para mantener la frase oculta y actualizada
    private String selectedPhrase;
    private char[] revealed;

    public GameUI() {
        super("Wheel of Fortune Game");
        // Inicializamos la lógica del juego.
        game = new Game();
        initGame(); // Carga la frase y prepara el panel
        initComponents(); // Configura la GUI
    }

    private void initGame() {
        // Selecciona la frase aleatoria y prepara el panel de letras
        selectedPhrase = game.getRandomPhrase(); // Considera hacer este método público
        revealed = new char[selectedPhrase.length()];
        for (int i = 0; i < selectedPhrase.length(); i++) {
            revealed[i] = selectedPhrase.charAt(i) == ' ' ? ' ' : '_';
        }
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLayout(new BorderLayout(10, 10));

        // Panel superior para mostrar el banner y el estado de la frase
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel banner = new JLabel("🎡 WHEEL OF FORTUNE GAME 🎡", SwingConstants.CENTER);
        banner.setFont(new Font("SansSerif", Font.BOLD, 24));
        topPanel.add(banner, BorderLayout.NORTH);

        phraseLabel = new JLabel(getFormattedPhrase(), SwingConstants.CENTER);
        phraseLabel.setFont(new Font("Monospaced", Font.BOLD, 28));
        topPanel.add(phraseLabel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // Panel central para botones y entrada de letras
        JPanel centerPanel = new JPanel();
        spinButton = new JButton("Girar la Ruleta");
        guessButton = new JButton("Adivinar Letra");
        letterInput = new JTextField(3);
        currentPlayerLabel = new JLabel("Turno: [Jugador]");

        centerPanel.add(spinButton);
        centerPanel.add(new JLabel("Letra:"));
        centerPanel.add(letterInput);
        centerPanel.add(guessButton);
        centerPanel.add(currentPlayerLabel);

        add(centerPanel, BorderLayout.CENTER);

        // Área inferior para mensajes y feedback
        messageArea = new JTextArea(6, 50);
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        add(scrollPane, BorderLayout.SOUTH);

        // Acciones de botones
        spinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Simula el giro de la ruleta y muestra el resultado
                try {
                    String sliceResult = game.randomSlice(); // Considera hacerlo público o crear un método en Game para girar la ruleta.
                    appendMessage("🎡 Resultado de la ruleta: " + sliceResult);
                } catch (Exception ex) {
                    appendMessage("❌ Error al girar la ruleta: " + ex.getMessage());
                }
            }
        });

        guessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String guessText = letterInput.getText().trim().toUpperCase();
                if (guessText.length() != 1) {
                    JOptionPane.showMessageDialog(GameUI.this, "Ingresa una sola letra.");
                    return;
                }
                char guessedLetter = guessText.charAt(0);
                boolean correctGuess = false;
                // Actualiza el panel según la letra adivinada
                for (int i = 0; i < selectedPhrase.length(); i++) {
                    char originalChar = selectedPhrase.charAt(i);
                    if (Character.toUpperCase(originalChar) == guessedLetter && revealed[i] == '_') {
                        revealed[i] = originalChar;
                        correctGuess = true;
                    }
                }
                if (correctGuess) {
                    appendMessage("¡Bien hecho! La letra " + guessedLetter + " está en la frase.");
                } else {
                    appendMessage("La letra " + guessedLetter + " no está en la frase. Se pasa el turno.");
                    // Llama al método para pasar turno
                    game.nextTurn();
                }
                letterInput.setText("");
                updateUIState();
                if (isPhraseComplete()) {
                    appendMessage("🎉 ¡Felicidades! Has completado la frase: " + selectedPhrase);
                    spinButton.setEnabled(false);
                    guessButton.setEnabled(false);
                }
            }
        });

        // Actualizamos el estado inicial
        updateUIState();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private String getFormattedPhrase() {
        StringBuilder sb = new StringBuilder();
        for (char c : revealed) {
            sb.append(c).append(' ');
        }
        return sb.toString();
    }

    private void updateUIState() {
        phraseLabel.setText(getFormattedPhrase());
        // Actualiza el nombre del jugador actual; para esto debes exponer un método en Game o Console
        String currentPlayer = game.getCurrentPlayerName(); // Considera hacerlo público en Game
        currentPlayerLabel.setText("Turno: " + currentPlayer);
    }

    private void appendMessage(String message) {
        messageArea.append(message + "\n");
    }

    private boolean isPhraseComplete() {
        for (char c : revealed) {
            if (c == '_') {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        // Registra jugadores mediante diálogos antes de iniciar la GUI, por ejemplo:
        SwingUtilities.invokeLater(() -> {
            // Ejemplo simple: pedir nombres con JOptionPane
            int numPlayers = Integer.parseInt(JOptionPane.showInputDialog("Ingresa el número de jugadores (mínimo 2):"));
            for (int i = 1; i <= numPlayers; i++) {
                String name = JOptionPane.showInputDialog("Ingresa el nombre del jugador " + i + ":");
                // Aquí debes registrar el jugador en el objeto Game. 
                // Puedes crear un método público en Game que permita agregar jugadores, o modificar registerPlayers.
                gameAddPlayer(name); // método estático simulado, deberás integrarlo según la lógica de Game.
            }
            new GameUI();
        });
    }

    // Ejemplo de método auxiliar para agregar jugadores al juego.
    // Tendrás que crear o adaptar el método en Game.
    private static void gameAddPlayer(String name) {
        // Por ejemplo, si Game tiene un método addPlayer:
        // game.addPlayer(new Player(name));
        // O, si no, modifica Game para permitir la incorporación de jugadores de forma individual.
    }
}
