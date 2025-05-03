package ui;

import game.*;
import model.state.GameState;
import players.Player;
import ui.panels.TopPanel;
import ui.panels.CenterPanel;
import ui.panels.BottomPanel;
import ui.panels.UsedLettersPanel;
import players.AutomaticPlayer;

import java.awt.Color;
import java.io.File;

import javax.swing.*;
import java.awt.*;

import java.util.List;


public class GameUI extends JFrame {
    private Game game;
    
    private boolean clientMode = false;
    private String clientHostInfo = "";
    private int connectedClients = 0;
    private JLabel statusLabel;
    public static network.GameServer serverInstance; // referencia pública para el host



    // Variables de estado del juego
    private String selectedPhrase;
    private char[] revealed;
    private boolean hasSpun;
    private boolean gameOver;
    private int currentSpinValue;
    private boolean isX2Active = false;
    private boolean hasExtraTurn = false;


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
        this(false); // El constructor sin parámetros asume que eres host
    }

    private void initGameState() {
        System.out.println("[GameUI] Inicializando estado del juego...");
        selectedPhrase = game.getRandomPhrase();
        game.setPhrase(selectedPhrase);

        revealed = new char[selectedPhrase.length()];
        for (int i = 0; i < selectedPhrase.length(); i++) {
            revealed[i] = (selectedPhrase.charAt(i) == ' ') ? ' ' : '_';
        }

        game.setRevealed(revealed.clone()); // ✅ CLAVE para evitar el null
        hasSpun = false;
        gameOver = false;
        currentSpinValue = 0;

        // ✅ solo aquí ya está todo listo para enviar
        if (serverInstance != null) {
            serverInstance.broadcastGameState(game);
        }
    }

    
    public void synchronizeUsedLetters(List<Character> usedLetters) {
        usedLettersPanel.clearLetters();
        for (Character c : usedLetters) {
            usedLettersPanel.addLetter(c);
        }
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
    
    
    public boolean isX2Active() {
        return isX2Active;
    }

    public void setX2Active(boolean x2Active) {
        this.isX2Active = x2Active;
    }

    public int getCurrentSpinValue() {
        return currentSpinValue;
    }

    public void setCurrentSpinValue(int value) {
        this.currentSpinValue = value;
    }
    
    public BottomPanel getBottomPanel() {
        return bottomPanel;
    }

    public CenterPanel getCenterPanel() {
        return centerPanel;
    }
 
    public void updateUIState() {
    	System.out.println("[GameUI] Actualizando UI state...");
        if (topPanel != null) topPanel.updatePhraseLabel();
        if (centerPanel != null) centerPanel.updateCurrentPlayer();
        if (centerPanel != null) centerPanel.updateWallets();
        checkAutomaticTurn();
        
        updateStatusLabel();
        
        if (!clientMode && centerPanel != null) {
            centerPanel.enableButtons(); // solo el host puede tocar botones
        }


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
                    bottomPanel.appendMessage("💸 " + currentPlayer.getName() + " has gone BANKRUPT! All money lost.", ColorPalette.HIGHLIGHT);
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
                if (sliceResult.equalsIgnoreCase("Extra Turn")) {
                    bottomPanel.appendMessage("🔄 You landed on EXTRA TURN! You won't lose your turn even if you guess wrong.", ColorPalette.INFO);
                    hasExtraTurn = true;
                }


                hasSpun = true;
            } catch (Exception ex) {
                bottomPanel.appendMessage("❌ Error spinning the wheel: " + ex.getMessage(), ColorPalette.ERROR);
            }
        }
    }

    public boolean guessLetter(String guessText) {
        if (gameOver || !hasSpun) return false;

        char guessedLetter = guessText.charAt(0);

        if (usedLettersPanel.isLetterUsed(guessedLetter)) {
            bottomPanel.appendMessage("❌ Letter '" + guessedLetter + "' has already been used. Try a different letter.", ColorPalette.ERROR);
            return false;
        }

        char upperLetter = Character.toUpperCase(guessedLetter);
        usedLettersPanel.addLetter(upperLetter);
        game.addUsedLetter(upperLetter);  // <--- esta es la clave


        if ("AEIOU".indexOf(guessedLetter) != -1) {
            bottomPanel.appendMessage("❌ You can only guess consonants in your turn! Try again.", ColorPalette.ERROR);
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
            bottomPanel.appendMessage("✔ Good! Letter '" + guessedLetter + "' is in the phrase (" + occurrences + " occurrence" + (occurrences > 1 ? "s" : "") + "). " + currentPlayer.getName() + " wins $" + amountWon + "! Total: $" + currentPlayer.getMoney(), ColorPalette.SUCCESS);

            updateUIState();
            if (isPhraseComplete()) {
                bottomPanel.appendMessage("🎉 Congratulations! The phrase is: " + selectedPhrase, ColorPalette.CUSTOM_PURPLE);
                gameOver = true;
                game.setRevealed(revealed);
                game.checkGameOver();
            }
        } else {
            bottomPanel.appendMessage("✖ Letter '" + guessedLetter + "' is not in the phrase. Next player!", ColorPalette.ERROR);
            isX2Active = false;

            if (hasExtraTurn) {
                bottomPanel.appendMessage("🛡️ EXTRA TURN active! You keep your turn.", ColorPalette.INFO);
                hasExtraTurn = false; // solo dura una ronda
            } else {
                game.nextTurn();
            }

            updateUIState();

        }
        

        hasSpun = false;
        game.setRevealed(revealed.clone());
        refreshPlayerCards();
        updateUIState();
        if (serverInstance != null) {
            serverInstance.broadcastGameState(game);
        }

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
            bottomPanel.appendMessage("❌ Not enough money to buy a vowel!", ColorPalette.WARNING);
            return;
        }

        currentPlayer.addMoney(-vowelPrice);
        refreshPlayerCards();
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
            bottomPanel.appendMessage("✔ The vowel '" + guessedVowel + "' appears " + occurrences + " time(s).", ColorPalette.SUCCESS);
            updateUIState();
            if (isPhraseComplete()) {
                bottomPanel.appendMessage("🎉 Congratulations! The phrase is: " + selectedPhrase, ColorPalette.CUSTOM_PURPLE);
                gameOver = true;
                game.setRevealed(revealed);
                game.checkGameOver();
            }
        } else {
            bottomPanel.appendMessage("❌ The vowel '" + guessedVowel + "' is NOT in the phrase.", ColorPalette.ERROR);
        }
        
        
        updateUIState();
        if (serverInstance != null) {
            serverInstance.broadcastGameState(game);
        }

    }

    public void attemptSolve(String solution) {
        if (gameOver) {
            bottomPanel.appendMessage("❌ The game is already over!", ColorPalette.ERROR);
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
            bottomPanel.appendMessage("❌ Incorrect solution! " + game.getCurrentPlayerName() + " loses their turn.", ColorPalette.ERROR);
            game.nextTurn();
            updateUIState();
        }
        
        if (serverInstance != null) {
            serverInstance.broadcastGameState(game);
        }

    }

    public void addUsedLetter(char letter) {
        usedLettersPanel.addLetter(letter);
    }

    private void registerPlayers() {
    	System.out.println("[GameUI] Registrando jugadores...");
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

            JPanel autoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            autoPanel.add(new JLabel("Automatic player?"));
            JCheckBox automaticCheckBox = new JCheckBox();
            autoPanel.add(automaticCheckBox);
            panel.add(autoPanel);

            panel.add(Box.createVerticalStrut(10));
            panel.add(new JLabel("Select your avatar:"));

            String[] avatarFileNames = {
                "avatar1.jpg", "avatar2.jpg", "avatar3.jpg", "avatar4.jpg", "avatar5.jpg"
            };

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

            Player player;
            if (automaticCheckBox.isSelected()) {
                player = new AutomaticPlayer(playerName);
            } else {
                player = new Player(playerName);
            }
            player.setAvatar(avatars[avatarChoice], avatarFileNames[avatarChoice]);
            game.addPlayer(player);
        }
        
        centerPanel.renderPlayerCards(game.getPlayers());

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
        refreshPlayerCards();

        // 4. Actualizar visualmente el juego.
        updateUIState();
    }
    
    public void synchronizeRevealed() {
        this.revealed = game.getRevealed().clone();
    }
    
    private void checkAutomaticTurn() {
        List<Player> players = game.getPlayers();
        if (players == null || players.isEmpty()) return;

        Player currentPlayer = players.get(game.getCurrentPlayerIndex());
        if (currentPlayer instanceof players.AutomaticPlayer) {
            Timer timer = new Timer(1000, e -> {
                ((players.AutomaticPlayer) currentPlayer).takeTurn(this);
            });
            timer.setRepeats(false);
            timer.start();
        }
    }


    public final class ColorPalette {
        // Darker variants for a more subdued look:
        public static final Color SPIN_RESULT = new Color(0, 0, 139);    // Dark Blue
        public static final Color SUCCESS = new Color(0, 100, 0);        // Dark Green
        public static final Color ERROR = new Color(139, 0, 0);          // Dark Red
        public static final Color WARNING = new Color(255, 140, 0);      // Dark Orange
        public static final Color INFO = new Color(0, 139, 139);         // Dark Cyan
        public static final Color HIGHLIGHT = new Color(139, 0, 139);      // Dark Magenta
        public static final Color CUSTOM_PURPLE = new Color(75, 0, 130);   // Indigo (Dark Purple)

        private ColorPalette() {
            // Prevent instantiation.
        }
    }
    
    public boolean hasExtraTurn() {
        return hasExtraTurn;
    }

    public void setExtraTurn(boolean extraTurn) {
        this.hasExtraTurn = extraTurn;
    }

    public void showAIStrategySelection() {
        String[] options = {"Easy", "Medium", "Hard"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Select AI Difficulty",
                "AI Settings",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        // Create a new strategy based on the user's choice.
        players.AutomaticPlayerStrategy newStrategy;
        switch (choice) {
            case 1:
                newStrategy = new players.MediumAIStrategy();
                break;
            case 2:
                newStrategy = new players.HardAIStrategy();
                break;
            case 0:
            default:
                newStrategy = new players.EasyAIStrategy();
                break;
        }

        // Update each automatic player with the new strategy.
        for (Player p : game.getPlayers()) {
            if (p instanceof players.AutomaticPlayer) {
                ((players.AutomaticPlayer) p).setStrategy(newStrategy);
            }
        }
        bottomPanel.appendMessage("AI difficulty set to: " + options[choice], ColorPalette.INFO);
    }
    
    public GameUI(boolean clientMode) {
        super("Wheel of Fortune Game");
        this.clientMode = clientMode;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setSize(900, 600);

        System.out.println("[GameUI] Inicializando GameUI...");

        this.game = Game.getInstance(this);
        usedLettersPanel = new UsedLettersPanel();

        // Crear la barra superior con menú
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);

        JMenu settingsMenu = new JMenu("Settings");
        settingsMenu.setFont(new Font("Arial", Font.BOLD, 14));
        settingsMenu.setForeground(Color.WHITE);
        settingsMenu.setOpaque(true);
        settingsMenu.setBackground(new Color(70, 130, 180));

        JMenuItem aiSettingsItem = new JMenuItem("AI Difficulty");
        aiSettingsItem.setFont(new Font("Arial", Font.BOLD, 14));
        aiSettingsItem.setBackground(new Color(100, 149, 237));
        aiSettingsItem.setForeground(Color.WHITE);
        aiSettingsItem.addActionListener(e -> showAIStrategySelection());

        settingsMenu.add(aiSettingsItem);
        menuBar.add(settingsMenu);
        setJMenuBar(menuBar);

        // Crear paneles antes de lógica del juego
        topPanel = new TopPanel(this);
        add(topPanel, BorderLayout.NORTH);

        centerPanel = new CenterPanel(this);
        add(centerPanel, BorderLayout.CENTER);

        bottomPanel = new BottomPanel(this);
        JPanel southContainer = new JPanel(new BorderLayout());
        southContainer.add(bottomPanel, BorderLayout.CENTER);
        southContainer.add(usedLettersPanel, BorderLayout.SOUTH);
        add(southContainer, BorderLayout.SOUTH);

        // ✅ SOLO EL HOST crea o carga partida
        if (!clientMode) {
            String[] options = {"Load saved game", "Create new game"};
            int choice = JOptionPane.showOptionDialog(
                this,
                "Do you want to load a saved game or create a new one?",
                "Load or New Game",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            );

            boolean isLoaded = false;
            if (choice == 0) {
                JFileChooser fileChooser = new JFileChooser("saved_games");
                fileChooser.setDialogTitle("Select a saved game to load");
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int result = fileChooser.showOpenDialog(this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    String filename = selectedFile.getName();

                    if (filename.endsWith(".json")) {
                        System.out.println("[GameUI] Archivo seleccionado para cargar: " + filename);
                        String saveName = filename.substring(0, filename.length() - 5);
                        game.loadGameState(saveName, this);
                        this.selectedPhrase = game.getSelectedPhrase();
                        this.revealed = game.getRevealed().clone();
                        synchronizeRevealed();
                        synchronizeUsedLetters(game.getUsedLetters());
                        isLoaded = true;
                        System.out.println("[GameUI] Partida cargada. Jugadores:");

                        if (serverInstance != null) {
                            serverInstance.broadcastGameState(game);
                        }

                    } else {
                        System.out.println("[GameUI] Archivo no válido. Creando nueva partida.");
                        JOptionPane.showMessageDialog(this, "Invalid file. Starting a new game.");
                        registerPlayers();
                        initGameState();

                        if (serverInstance != null) {
                            serverInstance.broadcastGameState(game);
                        }
                    }
                } else {
                    System.out.println("[GameUI] No se seleccionó archivo. Creando nueva partida.");
                    JOptionPane.showMessageDialog(this, "No file selected. Starting a new game.");
                    registerPlayers();
                    initGameState();

                    if (serverInstance != null) {
                        serverInstance.broadcastGameState(game);
                    }
                }
            } else {
                System.out.println("[GameUI] Se eligió crear nueva partida.");
                registerPlayers();
                initGameState();

                if (serverInstance != null) {
                    serverInstance.broadcastGameState(game);
                }
            }
        } else {
            // CLIENTE: Inicializa solo para recibir estado
            selectedPhrase = "";
            revealed = new char[0];
            hasSpun = false;
            gameOver = false;
            currentSpinValue = 0;
            disableInteraction(); // cliente no puede interactuar
        }

        if (!clientMode) {
            updateUIState(); // solo el host actualiza de inmediato
        }

        setLocationRelativeTo(null);
        setVisible(true);
    }


    
    public void setClientInfo(String host, int port) {
        this.clientHostInfo = host + ":" + port;
        repaint(); // For label update
    }

    public void setConnectedClients(int count) {
        this.connectedClients = count;
        repaint();
    }

    public void applyRemoteGameState(GameState state) {
        game.applyGameState(state);
        synchronizeRevealed();
        synchronizeUsedLetters(state.getUsedLetters());

        centerPanel.renderPlayerCards(game.getPlayers());
        

        updateUIState();
    }


    private void disableInteraction() {
        if (centerPanel != null) {
            centerPanel.disableButtons(); // esto sí desactiva solo los botones, de forma controlada
        }
    }

    
    public void setStatusLabel(JLabel label) {
        this.statusLabel = label;
    }

    public void updateStatusLabel() {
        if (statusLabel != null) {
            if (clientMode) {
                statusLabel.setText("MODE: Client | Connected to: " + clientHostInfo);
            } else {
                statusLabel.setText("MODE: Host | Clients connected: " + connectedClients);
            }
        }
    }

    public void refreshPlayerCards() {
        centerPanel.renderPlayerCards(game.getPlayers());
    }

    public void updateClientStatusLabel(int count) {
        if (statusLabel != null) {
            statusLabel.setText("Host | Clients Connected: " + count);
        }
    }


}
