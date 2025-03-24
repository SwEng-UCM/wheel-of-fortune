package ui;

import game.*;
import players.Player;
import ui.panels.TopPanel;
import ui.panels.CenterPanel;
import ui.panels.BottomPanel;
import ui.panels.UsedLettersPanel;

import javax.swing.*;
import java.awt.*;

public class GameUI extends JFrame {
    private Game game;

    // Variables de estado del juego
    private String selectedPhrase;
    private char[] revealed;
    private boolean hasSpun;
    private boolean gameOver;
    private int currentSpinValue;
    private boolean isX2Active = false;

    // Paneles
    private TopPanel topPanel;
    private CenterPanel centerPanel;
    private BottomPanel bottomPanel;
    private UsedLettersPanel usedLettersPanel;

    // Command Manager para soporte de Undo
    private CommandManager commandManager = new CommandManager();

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public GameUI() {
        super("Wheel of Fortune Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setSize(800, 500);

        this.game = Game.getInstance(this);

        registerPlayers();
        initGameState();

        topPanel = new TopPanel(this);
        add(topPanel, BorderLayout.NORTH);

        centerPanel = new CenterPanel(this);
        add(centerPanel, BorderLayout.CENTER);

        bottomPanel = new BottomPanel(this);
        usedLettersPanel = new UsedLettersPanel();
        JPanel southContainer = new JPanel(new BorderLayout());
        southContainer.add(bottomPanel, BorderLayout.CENTER);
        southContainer.add(usedLettersPanel, BorderLayout.SOUTH);
        add(southContainer, BorderLayout.SOUTH);

        updateUIState();
        setLocationRelativeTo(null);
        setVisible(true);
    }

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

    public Game getGame() {
        return game;
    }
    public UsedLettersPanel getUsedLettersPanel() {
        return usedLettersPanel;
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

    public void updateUIState() {
        topPanel.updatePhraseLabel();
        centerPanel.updateCurrentPlayer();
        centerPanel.updateWallets();
    }

    public void spinWheel() {
        if (!gameOver && !hasSpun) {
            try {
                String sliceResult = game.randomSlice();
                currentSpinValue = game.getSliceValue(sliceResult);
                bottomPanel.appendMessage("🎡 Spin result: " + sliceResult);

                if (sliceResult.equalsIgnoreCase("Bankrupt")) {
                    Player currentPlayer = game.getPlayers().get(game.getCurrentPlayerIndex());
                    currentPlayer.addMoney(-currentPlayer.getMoney());
                    bottomPanel.appendMessage("💸 " + currentPlayer.getName() + " has gone BANKRUPT! All money lost.");
                    isX2Active = false;
                    game.nextTurn();
                    updateUIState();
                    hasSpun = false;
                    return;
                }

                if (sliceResult.equalsIgnoreCase("Lose Turn")) {
                    bottomPanel.appendMessage("⛔ " + game.getCurrentPlayerName() + " has lost their turn! Next player.");
                    isX2Active = false;
                    game.nextTurn();
                    updateUIState();
                    hasSpun = false;
                    return;
                }

                if (sliceResult.equalsIgnoreCase("x2")) {
                    bottomPanel.appendMessage("✨ " + game.getCurrentPlayerName() + " landed on X2! Spin again...");
                    isX2Active = true;
                    spinWheel();
                    return;
                }

                hasSpun = true;
            } catch (Exception ex) {
                bottomPanel.appendMessage("❌ Error spinning the wheel: " + ex.getMessage());
            }
        }
    }

    public boolean guessLetter(String guessText) {
        if (gameOver || !hasSpun) return false;

        char guessedLetter = guessText.charAt(0);

        if (usedLettersPanel.isLetterUsed(guessedLetter)) {
            bottomPanel.appendMessage("❌ Letter '" + guessedLetter + "' has already been used. Try a different letter.");
            return false;
        }

        usedLettersPanel.addLetter(Character.toUpperCase(guessedLetter));

        if ("AEIOU".indexOf(guessedLetter) != -1) {
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

            if (isX2Active) {
                amountWon *= 2;
                isX2Active = false;
                bottomPanel.appendMessage("💥 X2 ACTIVE! " + currentPlayer.getName() + " wins DOUBLE money: $" + amountWon);
            }

            currentPlayer.addMoney(amountWon);
            bottomPanel.appendMessage("✔ Good! Letter '" + guessedLetter + "' is in the phrase (" + occurrences + " occurrence" + (occurrences > 1 ? "s" : "") + "). " + currentPlayer.getName() + " wins $" + amountWon + "! Total: $" + currentPlayer.getMoney());

            updateUIState();
            if (isPhraseComplete()) {
                bottomPanel.appendMessage("🎉 Congratulations! The phrase is: " + selectedPhrase);
                gameOver = true;
                game.setRevealed(revealed);
                game.checkGameOver();
            }
        } else {
            bottomPanel.appendMessage("✖ Letter '" + guessedLetter + "' is not in the phrase. Next player!");
            isX2Active = false;
            game.nextTurn();
            updateUIState();
        }

        hasSpun = false;
        game.setRevealed(revealed.clone());
        updateUIState();
        return occurrences > 0;
    }

    public boolean isPhraseComplete() {
        for (char c : revealed) {
            if (c == '_') return false;
        }
        return true;
    }

    public void buyVowel(String vowel) {
        Player currentPlayer = game.getPlayers().get(game.getCurrentPlayerIndex());

        int vowelPrice = 75;
        if (currentPlayer.getMoney() < vowelPrice) {
            bottomPanel.appendMessage("❌ Not enough money to buy a vowel!");
            return;
        }

        currentPlayer.addMoney(-vowelPrice);
        bottomPanel.appendMessage("🛒 " + currentPlayer.getName() + " bought the vowel '" + vowel + "' for $" + vowelPrice);

        char guessedVowel = vowel.charAt(0);
        int occurrences = 0;
        for (int i = 0; i < selectedPhrase.length(); i++) {
            char originalChar = selectedPhrase.charAt(i);
            if (Character.toUpperCase(originalChar) == guessedVowel && revealed[i] == '_') {
                revealed[i] = originalChar;
                occurrences++;
            }
        }

        if (occurrences > 0) {
            bottomPanel.appendMessage("✔ The vowel '" + guessedVowel + "' appears " + occurrences + " time(s).");
            updateUIState();
            if (isPhraseComplete()) {
                bottomPanel.appendMessage("🎉 Congratulations! The phrase is: " + selectedPhrase);
                gameOver = true;
                game.setRevealed(revealed);
                game.checkGameOver();
            }
        } else {
            bottomPanel.appendMessage("❌ The vowel '" + guessedVowel + "' is NOT in the phrase.");
        }

        updateUIState();
    }

    public void attemptSolve(String solution) {
        if (gameOver) {
            bottomPanel.appendMessage("❌ The game is already over!");
            return;
        }

        if (solution.equalsIgnoreCase(selectedPhrase)) {
            bottomPanel.appendMessage("🎉 " + game.getCurrentPlayerName() + " solved the puzzle! The phrase was: " + selectedPhrase);
            revealed = selectedPhrase.toCharArray();
            gameOver = true;
            game.setRevealed(revealed);
            updateUIState();
            game.checkGameOver();
        } else {
            bottomPanel.appendMessage("❌ Incorrect solution! " + game.getCurrentPlayerName() + " loses their turn.");
            game.nextTurn();
            updateUIState();
        }
    }

    public void addUsedLetter(char letter) {
        usedLettersPanel.addLetter(letter);
    }

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
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namePanel.add(new JLabel("Enter name for Player " + (i + 1) + ":"));
        JTextField nameField = new JTextField(20);
        namePanel.add(nameField);
        panel.add(namePanel);

        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Select your avatar:"));

        JPanel avatarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ImageIcon[] avatars = new ImageIcon[] {
            new ImageIcon(new ImageIcon(getClass().getResource("/avatar1.jpg")).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)),
            new ImageIcon(new ImageIcon(getClass().getResource("/avatar2.jpg")).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)),
            new ImageIcon(new ImageIcon(getClass().getResource("/avatar3.jpg")).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)),
            new ImageIcon(new ImageIcon(getClass().getResource("/avatar4.jpg")).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)),
            new ImageIcon(new ImageIcon(getClass().getResource("/avatar5.jpg")).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH))
        };

        JToggleButton[] avatarButtons = new JToggleButton[avatars.length];
        ButtonGroup avatarGroup = new ButtonGroup();
        for (int j = 0; j < avatars.length; j++) {
            avatarButtons[j] = new JToggleButton(avatars[j]);
            avatarButtons[j].setPreferredSize(new Dimension(60, 60));
            avatarGroup.add(avatarButtons[j]);
            avatarPanel.add(avatarButtons[j]);
        }
        avatarButtons[0].setSelected(true);
        panel.add(avatarPanel);

        int result = JOptionPane.showConfirmDialog(this, panel, "Player Registration", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            i--;
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

        Player player = new Player(playerName);
        player.setAvatar(avatars[avatarChoice]);
        game.addPlayer(player);
    }
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameUI::new);
    }
    
    public void undoLastGuess(char letterToUndo, char[] previousRevealed, int previousPlayerMoney, int playerIndex) {
        // 1. Eliminar la letra del panel visual de letras usadas.
        usedLettersPanel.removeLetter(letterToUndo);

        // 2. Restaurar la frase revelada al estado anterior.
        this.revealed = previousRevealed.clone(); 

        // 3. Restaurar el dinero del jugador al estado anterior.
        Player player = game.getPlayers().get(playerIndex);
        int moneyDiff = previousPlayerMoney - player.getMoney();
        player.addMoney(moneyDiff);

        // 4. Actualizar visualmente el juego.
        updateUIState();
        bottomPanel.appendMessage("↩️ Undo performed: Letter '" + letterToUndo + "' removed.");
    }
    public void synchronizeRevealed() {
        this.revealed = game.getRevealed().clone();
    }


}
