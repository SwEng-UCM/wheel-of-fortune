package players;

import ui.GameUI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AutomaticPlayer extends Player {
    private Random random;

    public AutomaticPlayer(String name) {
        super(name);
        this.random = new Random();
    }
    
    /**
     * Automatically plays a turn using the GUI.
     * It spins the wheel and then automatically picks an available consonant.
     *
     * @param ui the current GameUI instance.
     */
    public void takeTurn(GameUI ui) {
        // Display message in the bottom panel indicating the automatic turn.
        ui.getBottomPanel().appendMessage(getName() + " (Automatic) is taking a turn...");
        
        // Simulate spinning the wheel.
        ui.spinWheel();
        
        // Choose a letter automatically.
        char guess = chooseLetter(ui);
        ui.getBottomPanel().appendMessage(getName() + " automatically guesses the letter: " + guess);
        
        // Process the guess.
        ui.guessLetter(String.valueOf(guess));
    }
    
    /**
     * Chooses a random consonant that has not been used yet.
     *
     * @param ui the current GameUI instance.
     * @return a randomly selected available consonant.
     */
    private char chooseLetter(GameUI ui) {
        List<Character> available = new ArrayList<>();
        String vowels = "AEIOU";
        // Iterate over A-Z and pick letters that are not vowels and not yet used.
        for (char c = 'A'; c <= 'Z'; c++) {
            if (vowels.indexOf(c) != -1) continue; // skip vowels
            // Use the UsedLettersPanel to check if the letter has been used.
            if (!ui.getUsedLettersPanel().isLetterUsed(c)) {
                available.add(c);
            }
        }
        if (available.isEmpty()) {
            // Fallback in case no letter is available.
            return 'B';
        }
        return available.get(random.nextInt(available.size()));
    }
}

