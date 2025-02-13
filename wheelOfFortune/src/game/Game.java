package game;

import players.Player;
import ui.Console;
import java.util.ArrayList;
import java.util.List;

public class Game {
    private List<Player> players;
    private int NumWords;
    private int currentPlayerIndex; // Track the current player's turn

    public Game() {
        players = new ArrayList<>();
        NumWords = 0;
        currentPlayerIndex = 0;
    }

    public void start() {
        Console.showMessage("Welcome to the Wheel of Fortune game!");
        players = Console.registerPlayers();
        Console.showMessage("Players registered successfully!");
        assignTurn(); // Assign the first turn automatically
    }

    public void pannel(String frase) {
        NumWords = Console.pannelCounter(frase);
        Console.showMessage("This panel contains " + NumWords + " words");
        for (int i = 0; i < NumWords; i++) {
            if (frase.charAt(i) != ' ') {
                Console.showMessageInLine("|_|");
            } else {
                Console.showMessageInLine(" ");
            }
        }
    }

    public void spinWheel() {
        Console.showMessage("Spin the wheel!!!");
    }

    public void assignTurn() {
        if (!players.isEmpty()) {
            Player currentPlayer = players.get(currentPlayerIndex);
            Console.showMessage("It's " + currentPlayer.getName() + "'s turn!");
        }
    }

    public void nextTurn() {
        if (!players.isEmpty()) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            assignTurn();
        }
    }
}