package players;

import ui.GameUI;
import java.awt.Color;

/**
 * HardAIStrategy - the most advanced AI strategy in this simple example.
 * Always selects the highest-frequency available letter.
 */
public class HardAIStrategy implements AutomaticPlayerStrategy {
    // Frequency order as before.
    private final char[] frequencyOrder = {'E','T','A','O','I','N','S','H','R','D','L','U','C','M','F','W','Y','P','B','V','K','J','X','Q','Z'};

    @Override
    public void performTurn(GameUI ui, AutomaticPlayer player) {
        ui.getBottomPanel().appendMessage(player.getName() + " [Hard] is taking its turn...", Color.BLACK);
        ui.spinWheel();

        // Always choose the most frequent letter that hasn't been used.
        for (char c : frequencyOrder) {
            if (!ui.getUsedLettersPanel().isLetterUsed(c)) {
                ui.getBottomPanel().appendMessage(player.getName() + " guessed: " + c, Color.BLUE);
                ui.guessLetter(String.valueOf(c));
                return;
            }
        }
    }
}
