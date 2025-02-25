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
    private int NumWords;
    private int currentPlayerIndex;
    private List<String> phrases;
    private List<String> slices;

    public Game() {
        players = new ArrayList<>();
        NumWords = 0;
        currentPlayerIndex = 0;
        phrases = new ArrayList<>();
        slices = new ArrayList<>();
        loadPhrasesFromFile("phrases.txt");
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

    public void start() {
        Console.clearScreen();
        Console.showBanner();
        Console.showMessage("\n🎮 WELCOME TO THE WHEEL OF FORTUNE GAME! 🎮\n");
        players = Console.registerPlayers();
        Console.showMessage("\n✅ Players registered successfully!\n");
        assignTurn();
        startGameRound();
    }

    public void startGameRound() {
        if (phrases.isEmpty()) {
            Console.showMessage("⚠ No phrases available. Please check the file.");
            return;
        }
        String selectedPhrase = getRandomPhrase();
        pannel(selectedPhrase);
        spinWheel();
    }

    private String getRandomPhrase() {
        Random random = new Random();
        return phrases.get(random.nextInt(phrases.size()));
    }

    public void pannel(String frase) {
        Console.showMessage("\n🎭 THE SECRET PHRASE 🎭\n");
        Console.showMessage("This panel contains " + frase.length() + " characters\n");

        String separator = "━".repeat(frase.length() * 3 + 1);  // Ajusta el ancho a la frase

        Console.showMessage(separator); // Línea superior

        for (int i = 0; i < frase.length(); i++) {
            if (frase.charAt(i) != ' ') {
                Console.showMessageInLine("|_| ");
            } else {
                Console.showMessageInLine("   ");
            }
        }

        Console.showMessage(""); // Salto de línea
        Console.showMessage(separator); // Línea inferior

        askForLetter();
        nextTurn();
    }

    public void spinWheel() {
        String slice;
    	Console.showMessage("\n🎡 SPIN THE WHEEL!!! 🎡\n");
    	loadSlices("slices.txt");
        slice = randomSlice();
        Console.showMessage(slice);
        askForLetter();
        nextTurn();
    }

    
    private String randomSlice() {
    	if(slices != null && !slices.isEmpty()) {
	    	Random random = new Random();
	    	return slices.get(random.nextInt(slices.size()));
    	} else {
    		throw new IllegalStateException("The list is yet to be initializised"); 
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
            Console.showMessage("❌ Error loading phrases: " + e.getMessage());
        }
    }
    
    public void assignTurn() {
        if (!players.isEmpty()) {
            Player currentPlayer = players.get(currentPlayerIndex);
            Console.showMessage("\n🎯 It's " + currentPlayer.getName() + "'s turn! 🎯\n");
        }
    }

    public void nextTurn() {
        if (!players.isEmpty()) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            assignTurn();
        }
    }

    private void askForLetter() {
        InputHelper.getText("\n🔠 Enter a letter: ");
    }
}
