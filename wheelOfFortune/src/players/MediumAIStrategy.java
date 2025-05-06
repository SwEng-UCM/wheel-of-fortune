package players;

import ui.GameUI;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * MediumAIStrategy - an AI strategy that uses letter frequency (with some randomness)
 * to select a letter. More sophisticated than Easy.
 */
public class MediumAIStrategy implements AutomaticPlayerStrategy {
    private Random random = new Random();
    // Frequency order for letters (most common first) for basic English letter frequency.
    private final char[] frequencyOrder = {
        'T','N','S','H','R','D','L','C','M','F','W','Y','P','B','V','K','J','X','Q','Z'
    };

    @Override
    public void performTurn(GameUI ui, AutomaticPlayer player) {
        ui.getBottomPanel().appendMessage(player.getName() + " [Medium] is taking its turn...", Color.BLACK);
        ui.spinWheel();

        // Build a list of available consonants from the frequency order.
        List<Character> available = new ArrayList<>();
        for (char c : frequencyOrder) {
            if (!ui.getUsedLettersPanel().isLetterUsed(c)) {
                available.add(c);
            }
        }

        // If no consonants remain, attempt to buy a vowel.
        if (available.isEmpty()) {
            for (char v : new char[]{'A','E','I','O','U'}) {
                if (!ui.getUsedLettersPanel().isLetterUsed(v) 
                    && player.getMoney() >= 75) {
                    ui.getBottomPanel()
                      .appendMessage(player.getName() + " buys vowel: " + v, Color.BLUE);
                    ui.buyVowel(String.valueOf(v));
                    return;
                }
            }
            // No valid consonant or vowel action possible
            return;
        }

        // Add some randomness by choosing one from the top half of the list.
        int count = available.size();
        int index = random.nextInt(Math.max(1, count / 2));
        char chosen = available.get(index);
        ui.getBottomPanel().appendMessage(player.getName() + " guessed: " + chosen, Color.BLUE);
        ui.guessLetter(String.valueOf(chosen));
    }
}