package game;

import players.Player;
import ui.Console;
import utils.InputHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Game {
    private List<Player> players;
    private int currentPlayerIndex;
    private List<String> phrases;
    private List<String> slices;

    public Game() {
        players = new ArrayList<>();
        currentPlayerIndex = 0;
        phrases = new ArrayList<>();
        slices = new ArrayList<>();
        loadPhrasesFromFile("phrases.txt");
        loadSlices("slices.txt"); // Carga de slices una sola vez
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

        String selectedPhrase = getRandomPhrase();

        // Inicializa el panel con '_' para letras y espacios en blanco donde corresponda
        char[] revealed = new char[selectedPhrase.length()];
        for (int i = 0; i < selectedPhrase.length(); i++) {
            if (selectedPhrase.charAt(i) == ' ') {
                revealed[i] = ' ';
            } else {
                revealed[i] = '_';
            }
        }

        // Bucle principal del juego
        while (!isPhraseComplete(revealed)) {
            // Muestra el estado actual de la frase oculta
            displayPanel(revealed);
            // Indica de quién es el turno
            assignTurn();

            // El jugador actual gira la ruleta
            InputHelper.getText("\nPress Enter to spin the wheel...");
            String sliceResult = randomSlice();
            int wheelValue = getSliceValue(sliceResult);
            Console.showMessage("\n🎡 SPIN THE WHEEL!!! 🎡\n" + sliceResult);

            // Solicita la letra al jugador actual
            String guess = InputHelper.getText("\n🔠 " + getCurrentPlayerName() + ", enter a letter: ");
            if (guess.length() != 1) {
                Console.showMessage("Please enter a single letter.");
                continue; // Permite reintentar el turno actual
            }
            char guessedLetter = Character.toUpperCase(guess.charAt(0));
            boolean correctGuess = false;

            // Actualiza el panel si la letra se encuentra en la frase
            for (int i = 0; i < selectedPhrase.length(); i++) {
                char originalChar = selectedPhrase.charAt(i);
                if (Character.toUpperCase(originalChar) == guessedLetter && revealed[i] == '_') {
                    revealed[i] = originalChar;
                    correctGuess = true;
                }
            }
            if (correctGuess) {
                Player currentPlayer = players.get(currentPlayerIndex);
                currentPlayer.addMoney(wheelValue); // Se suma el dinero del giro de la ruleta
                Console.showMessage("Good job! The letter " + guessedLetter + " is in the phrase.");
                Console.showMessage(currentPlayer.getName() + " wins " + wheelValue + " money! Total: " + currentPlayer.getMoney());
            } else {
                Console.showMessage("Sorry, the letter " + guessedLetter + " is not in the phrase.");
                // Si falla, se pasa al siguiente turno
                nextTurn();
            }
        }
        // Se muestra el panel final y se felicita al ganador
        displayPanel(revealed);
        Console.showMessage("\n🎉 Congratulations! The phrase is complete: " + selectedPhrase);
    }

    private boolean isPhraseComplete(char[] revealed) {
        for (char c : revealed) {
            if (c == '_') {
                return false;
            }
        }
        return true;
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
        if (slices != null && !slices.isEmpty()) {
            Random random = new Random();
            return slices.get(random.nextInt(slices.size()));
        } else {
            throw new IllegalStateException("The slices list is not initialized.");
        }
    }

    public int getSliceValue(String slice) {
        try {
            return Integer.parseInt(slice.replaceAll("[^0-9]", "")); // Extrae el valor numérico de la ruleta
        } catch (NumberFormatException e) {
            return 0; // En caso de que el slice no sea un número, devuelve 0
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
        }
    }

    public String getCurrentPlayerName() {
        if (!players.isEmpty()) {
            return players.get(currentPlayerIndex).getName();
        }
        return "Unknown";
    }
}
