package players;

import ui.GameUI;
import java.awt.Color;

/**
 * HardAIStrategy - the most advanced AI strategy in this simple example.
 * Always selects the highest-frequency available letter, and if none remain,
 * attempts to buy a vowel.
 */
public class HardAIStrategy implements AutomaticPlayerStrategy {
    // Frequency order for consonants (most common first).
    private final char[] frequencyOrder = {
        'T','N','S','H','R','D','L','C','M','F','W','Y','P','B','V','K','J','X','Q','Z'
    };

    @Override
    public void performTurn(GameUI ui, AutomaticPlayer player) {
        ui.getBottomPanel().appendMessage(player.getName() + " [Hard] is taking its turn...", Color.BLACK);
        ui.spinWheel();

        // Try guessing the most frequent unused consonant.
        for (char c : frequencyOrder) {
            if (!ui.getUsedLettersPanel().isLetterUsed(c)) {
                ui.getBottomPanel().appendMessage(player.getName() + " guessed: " + c, Color.BLUE);
                ui.guessLetter(String.valueOf(c));
                return;
            }
        }

        // No consonants left -> attempt to buy a vowel if affordable.
        for (char v : new char[]{'A','E','I','O','U'}) {
            if (!ui.getUsedLettersPanel().isLetterUsed(v)
                && player.getMoney() >= 75) {
                ui.getBottomPanel()
                  .appendMessage(player.getName() + " compra vocal: " + v, Color.BLUE);
                ui.buyVowel(String.valueOf(v));
                return;
            }
        }

        // If no action possible (no consonants or unaffordable/used vowels), end turn.
    }
}