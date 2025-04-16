package players;

import ui.GameUI;

/**
 * This interface defines the contract for the automatic player AI strategies.
 * Each concrete strategy must implement the performTurn() method.
 */
public interface AutomaticPlayerStrategy {
    /**
     * Performs the AI move for the given AutomaticPlayer.
     * @param ui The current GameUI instance, allowing interaction with the game.
     * @param player The AutomaticPlayer that is taking the turn.
     */
    void performTurn(GameUI ui, AutomaticPlayer player);
}

