package players;

import ui.GameUI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import players.EasyAIStrategy;
import players.AutomaticPlayerStrategy;

public class AutomaticPlayer extends Player {
    private Random random;
    private AutomaticPlayerStrategy strategy;

    public AutomaticPlayer(String name) {
        super(name);
        this.random = new Random();
        // Set a default strategy (Easy) when the player is created.
        this.strategy = new EasyAIStrategy();
    }
    /**
     * Sets the AI strategy for this automatic player.
     * This method lets you change the AI difficulty at runtime.
     *
     * @param strategy The new strategy to use.
     */
    public void setStrategy(AutomaticPlayerStrategy strategy) {
        this.strategy = strategy;
    }
    
    /**
     * Takes a turn by delegating to the current AI strategy.
     * This uses the Strategy pattern: 
     * the algorithm for taking a turn is encapsulated in a separate strategy object.
     *
     * @param ui The current GameUI instance.
     */
    public void takeTurn(GameUI ui) {
        if (strategy != null) {
            strategy.performTurn(ui, this);
        } else {
            // Fallback: if no strategy is set, show an error (should not happen).
            ui.getBottomPanel().appendMessage(getName() + " (Automatic) is taking a turn... (No strategy set)", java.awt.Color.RED);
        }
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

