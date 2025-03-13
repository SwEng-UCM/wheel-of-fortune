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

        this.game = new Game(this); // 🔹 Pasar referencia de la ventana actual

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

                if (sliceResult.equalsIgnoreCase("Bankrupt")) {
                    Player currentPlayer = game.getPlayers().get(game.getCurrentPlayerIndex());
                    currentPlayer.addMoney(-currentPlayer.getMoney()); // Pierde todo su dinero
                    bottomPanel.appendMessage("💸 " + currentPlayer.getName() + " has gone BANKRUPT! All money lost.");
                    
                    game.nextTurn(); // Pasar al siguiente jugador
                    updateUIState();
                    hasSpun = false;
                    return;
                }

                if (sliceResult.equalsIgnoreCase("Lose Turn")) {
                    bottomPanel.appendMessage("⛔ " + game.getCurrentPlayerName() + " has lost their turn! Next player.");
                    
                    game.nextTurn(); // Pasar al siguiente jugador
                    updateUIState();
                    hasSpun = false;
                    return;
                }

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

    // Lista de vocales
    String vowels = "AEIOU";
    if (vowels.indexOf(guessedLetter) != -1) {
        bottomPanel.appendMessage("❌ You can only guess consonants in your turn! Try again.");
        return false;
    }

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
            game.setRevealed(revealed);
            game.checkGameOver();
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
            // Creamos un panel custom para que el usuario introduzca su nombre y seleccione un avatar
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            
            // Panel para el nombre
            JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            namePanel.add(new JLabel("Enter name for Player " + (i + 1) + ":"));
            JTextField nameField = new JTextField(20);
            namePanel.add(nameField);
            panel.add(namePanel);
            
            panel.add(Box.createVerticalStrut(10));
            panel.add(new JLabel("Select your avatar:"));
            
            // Panel para la selección de avatares
            JPanel avatarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            // Cargamos y escalamos los 5 avatares (ajusta el tamaño deseado, aquí 50x50 píxeles)
            ImageIcon[] avatars = new ImageIcon[] {
                new ImageIcon(new ImageIcon(getClass().getResource("/avatar1.jpg")).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)),
                new ImageIcon(new ImageIcon(getClass().getResource("/avatar2.jpg")).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)),
                new ImageIcon(new ImageIcon(getClass().getResource("/avatar3.jpg")).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)),
                new ImageIcon(new ImageIcon(getClass().getResource("/avatar4.jpg")).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)),
                new ImageIcon(new ImageIcon(getClass().getResource("/avatar5.jpg")).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH))
            };
            
            // Usamos botones de alternancia para la selección
            JToggleButton[] avatarButtons = new JToggleButton[avatars.length];
            ButtonGroup avatarGroup = new ButtonGroup();
            for (int j = 0; j < avatars.length; j++) {
                avatarButtons[j] = new JToggleButton(avatars[j]);
                avatarButtons[j].setPreferredSize(new Dimension(60, 60));
                avatarGroup.add(avatarButtons[j]);
                avatarPanel.add(avatarButtons[j]);
            }
            // Selecciona por defecto el primer avatar
            avatarButtons[0].setSelected(true);
            
            panel.add(avatarPanel);
            
            int result = JOptionPane.showConfirmDialog(this, panel, "Player Registration", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) {
                i--; // Si se cancela, volvemos a intentar para este jugador
                continue;
            }
            
            String playerName = nameField.getText().trim();
            if (playerName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                i--;
                continue;
            }
            
            int avatarChoice = 0;
            for (int j = 0; j < avatarButtons.length; j++) {
                if (avatarButtons[j].isSelected()) {
                    avatarChoice = j;
                    break;
                }
            }
            
            // Creamos el jugador y le asignamos el avatar seleccionado
            Player player = new Player(playerName);
            player.setAvatar(avatars[avatarChoice]);
            game.addPlayer(player);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameUI::new);
    }
}

