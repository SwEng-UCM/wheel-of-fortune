package game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.state.GameState;
import model.state.PlayerState;
import players.Player;
import ui.Console;
import ui.EndScreen;
import ui.GameUI;
import utils.InputHelper;

import javax.swing.*;
import java.io.*;
import java.util.*;

public class Game {
    private static volatile Game instance;

    private List<Player> players;
    private int currentPlayerIndex;
    private List<String> phrases;
    private List<String> slices;
    private Player currentPlayer;
    private List<Character> usedLetters = new ArrayList<>();

    private String phrase;
    private char[] revealed;
    private boolean isGameOver;
    private JFrame gameWindow;
    private boolean hasSpun;

    private Game(JFrame gameWindow) {
        this.gameWindow = gameWindow;
        players = new ArrayList<>();
        currentPlayerIndex = 0;
        phrases = new ArrayList<>();
        slices = new ArrayList<>();
        loadPhrasesFromFile("phrases.txt");
        loadSlices("slices.txt");
    }

    public static Game getInstance(JFrame gameWindow) {
        if (instance == null) {
            synchronized (Game.class) {
                if (instance == null) {
                    instance = new Game(gameWindow);
                }
            }
        }
        return instance;
    }

    private void loadPhrasesFromFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            Console.showMessage("❌ ERROR: The file " + fileName + " does not exist in the directory.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    phrases.add(line.trim());
                }
            }
        } catch (IOException e) {
            Console.showMessage("❌ Error loading phrases: " + e.getMessage());
        }
    }

    private void loadSlices(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            Console.showMessage("❌ ERROR: The file " + fileName + " does not exist in the directory.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    slices.add(line.trim());
                }
            }
        } catch (IOException e) {
            Console.showMessage("❌ Error loading slices: " + e.getMessage());
        }
    }

    public void setPhrase(String puzzle) {
        this.phrase = puzzle;
    }

    public String getSelectedPhrase() {
        return phrase;
    }

    public void start() {
        Console.clearScreen();
        Console.showBanner();
        Console.showMessage("\n🎮 WELCOME TO THE WHEEL OF FORTUNE GAME! 🎮\n");
        players = Console.registerPlayers();
        Console.showMessage("\n✅ Players registered successfully!\n");
        startGameRound();
    }

    public void startGameRound() {
        if (phrases.isEmpty()) {
            Console.showMessage("⚠ No phrases available. Please check the file.");
            return;
        }

        this.phrase = getRandomPhrase();

        revealed = new char[phrase.length()];
        usedLetters.clear();
        for (int i = 0; i < phrase.length(); i++) {
            revealed[i] = (phrase.charAt(i) == ' ') ? ' ' : '_';
        }

        while (!isPhraseComplete(revealed)) {
            displayPanel(revealed);
            assignTurn();

            InputHelper.getText("\nPress Enter to spin the wheel...");
            String sliceResult = randomSlice();
            int wheelValue = getSliceValue(sliceResult);
            Console.showMessage("\n🎡 SPIN THE WHEEL!!! 🎡\n" + sliceResult);

            hasSpun = true;

            String guess = InputHelper.getText("\n🔠 " + getCurrentPlayerName() + ", enter a letter: ");
            if (guess.length() != 1) {
                Console.showMessage("Please enter a single letter.");
                continue;
            }
            char guessedLetter = Character.toUpperCase(guess.charAt(0));
            if (!usedLetters.contains(guessedLetter)) {
                usedLetters.add(guessedLetter);
            }
            boolean correctGuess = false;

            for (int i = 0; i < phrase.length(); i++) {
                char originalChar = phrase.charAt(i);
                if (Character.toUpperCase(originalChar) == guessedLetter && revealed[i] == '_') {
                    revealed[i] = originalChar;
                    correctGuess = true;
                }
            }
            if (correctGuess) {
                currentPlayer.addMoney(wheelValue);
                Console.showMessage("Good job! The letter " + guessedLetter + " is in the phrase.");
                Console.showMessage(currentPlayer.getName() + " wins " + wheelValue + " money! Total: " + currentPlayer.getMoney());
            } else {
                Console.showMessage("Sorry, the letter " + guessedLetter + " is not in the phrase.");
                nextTurn();
            }
        }

        displayPanel(revealed);
        Console.showMessage("\n🎉 Congratulations! The phrase is complete: " + phrase);
        checkGameOver();
    }

    private boolean isPhraseComplete(char[] revealed) {
        for (char c : revealed) {
            if (c == '_') {
                return false;
            }
        }
        return true;
    }

    private boolean phraseIsComplete() {
        return isPhraseComplete(revealed);
    }

    public void checkGameOver() {
        if (revealed == null) {
            System.out.println("⚠️ ERROR: revealed is null in checkGameOver()");
            return;
        }
        boolean phraseCompleted = phraseIsComplete();
        if (phraseCompleted) {
            isGameOver = true;
            Player winner = players.get(currentPlayerIndex);

            SwingUtilities.invokeLater(() ->
                    new EndScreen(winner, this, getSelectedPhrase()).setVisible(true)
            );
        }
    }

    public void setRevealed(char[] updatedRevealed) {
        this.revealed = updatedRevealed;
    }

    private void displayPanel(char[] revealed) {
        Console.showMessage("\n🎭 THE SECRET PHRASE 🎭");
        StringBuilder panel = new StringBuilder();
        for (char c : revealed) {
            panel.append(c).append(' ');
        }
        Console.showMessage(panel.toString());
    }

    public String getRandomPhrase() {
        Random random = new Random();
        return phrases.get(random.nextInt(phrases.size()));
    }

    public String randomSlice() {
        if (!slices.isEmpty()) {
            Random random = new Random();
            return slices.get(random.nextInt(slices.size()));
        } else {
            throw new IllegalStateException("The slices list is not initialized.");
        }
    }

    public int getSliceValue(String slice) {
        try {
            return Integer.parseInt(slice.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void assignTurn() {
        if (!players.isEmpty()) {
            Player currentPlayer = players.get(currentPlayerIndex);
            Console.showMessage("\n🎯 It's " + currentPlayer.getName() + "'s turn! 🎯");
        }
    }

    public void nextTurn() {
        if (!players.isEmpty()) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            hasSpun = false;
        }
    }

    public String getCurrentPlayerName() {
        return (!players.isEmpty()) ? players.get(currentPlayerIndex).getName() : "Unknown";
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void restartGame() {
        players.clear();
        currentPlayerIndex = 0;
        isGameOver = false;
        hasSpun = false;
        usedLetters.clear();

        if (gameWindow != null) {
            gameWindow.dispose();
        }

        SwingUtilities.invokeLater(() -> {
            GameUI newGameUI = new GameUI();
            setGameWindow(newGameUI);
        });
    }

    public JFrame getGameWindow() {
        return gameWindow;
    }

    public void setGameWindow(JFrame window) {
        this.gameWindow = window;
    }

    public char[] getRevealed() {
        return revealed;
    }

    public void setCurrentPlayerIndex(int index) {
        this.currentPlayerIndex = index;
    }

    public GameState createGameState() {
        List<PlayerState> playerStates = new ArrayList<>();
        for (Player player : players) {
            playerStates.add(new PlayerState(player.getName(), player.getAvatarKey(), player.getMoney()));
        }
        List<Character> revealedList = new ArrayList<>();
        for (char c : revealed) {
            revealedList.add(c);
        }
        return new GameState(playerStates, phrase, revealedList, usedLetters, currentPlayerIndex);
    }

    public void applyGameState(GameState state) {
        List<Player> loadedPlayers = new ArrayList<>();
        for (PlayerState ps : state.getPlayers()) {
            loadedPlayers.add(new Player(ps.getName(), ps.getAvatar(), ps.getMoney()));
        }
        this.players = loadedPlayers;
        this.phrase = state.getWordToGuess();
        List<Character> loadedRevealed = state.getRevealedLetters();
        this.revealed = new char[loadedRevealed.size()];
        for (int i = 0; i < loadedRevealed.size(); i++) {
            this.revealed[i] = loadedRevealed.get(i);
        }
        this.usedLetters = state.getUsedLetters();
        this.currentPlayerIndex = state.getCurrentPlayerIndex();
    }

    public void saveGameState(String name) {
        GameState state = createGameState();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        File dir = new File("saved_games");
        if (!dir.exists()) {
            dir.mkdir();
        }

        try (FileWriter writer = new FileWriter("saved_games/" + name + ".json")) {
            gson.toJson(state, writer);
            JOptionPane.showMessageDialog(null, "✅ Game saved as '" + name + ".json'.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "❌ Failed to save game: " + e.getMessage());
        }
    }

    public void loadGameState(String name, GameUI gameUI) {
        Gson gson = new Gson();

        File file = new File("saved_games/" + name + ".json");
        if (!file.exists()) {
            JOptionPane.showMessageDialog(null, "❌ Save file '" + name + ".json' does not exist.");
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            GameState state = gson.fromJson(reader, GameState.class);
            applyGameState(state);
            gameUI.synchronizeRevealed();
            JOptionPane.showMessageDialog(null, "📂 Game '" + name + ".json' loaded successfully.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "❌ Failed to load game: " + e.getMessage());
        }
    }

}