package players;

import ui.GameUI;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * EasyAIStrategy - a simple AI algorithm that chooses a random available consonant.
 */
public class EasyAIStrategy implements AutomaticPlayerStrategy {
    private Random random = new Random();

    @Override
    public void performTurn(GameUI ui, AutomaticPlayer player) {
        // Show a message indicating that the AI with easy difficulty is taking its turn.
        ui.getBottomPanel().appendMessage(player.getName() + " [Easy] is taking its turn...", Color.BLACK);
        // Simulate spinning the wheel.
        ui.spinWheel();

        // Build a list of all available consonants.
        List<Character> available = new ArrayList<>();
        String vowels = "AEIOU";
        for (char c = 'A'; c <= 'Z'; c++) {
            if (vowels.indexOf(c) != -1) continue; // Skip vowels
            if (!ui.getUsedLettersPanel().isLetterUsed(c)) {
                available.add(c);
            }
        }
        // If no letter is available, do nothing.
        if (available.isEmpty()) return;

        // Randomly choose a letter from the available list.
        char chosen = available.get(random.nextInt(available.size()));
        ui.getBottomPanel().appendMessage(player.getName() + " guessed: " + chosen, Color.BLUE);
        // Process the guess.
        ui.guessLetter(String.valueOf(chosen));
    }
}

