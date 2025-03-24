package game;

import players.Player;
import ui.GameUI;

public class BuyVowelCommand implements Command {
    private GameUI gameUI;
    private char vowel;
    private char[] previousRevealed;
    private int currentPlayerIndex;
    private int previousMoney;

    public BuyVowelCommand(GameUI gameUI, char vowel) {
        this.gameUI = gameUI;
        this.vowel = vowel;
    }

    @Override
    public void execute() {
        Game game = gameUI.getGame();
        currentPlayerIndex = game.getCurrentPlayerIndex();
        Player currentPlayer = game.getPlayers().get(currentPlayerIndex);
        previousRevealed = gameUI.getRevealed().clone();
        previousMoney = currentPlayer.getMoney();

        gameUI.buyVowel(String.valueOf(vowel));
    }

    @Override
    public void undo() {
        Game game = gameUI.getGame();
        Player currentPlayer = game.getPlayers().get(currentPlayerIndex);

        // Restaurar dinero
        currentPlayer.addMoney(previousMoney - currentPlayer.getMoney());

        // Restaurar frase revelada
        game.setRevealed(previousRevealed.clone());
        gameUI.synchronizeRevealed();

        // Quitar la vocal del panel de letras usadas
        gameUI.getUsedLettersPanel().removeLetter(vowel);

        // Refrescar la interfaz
        gameUI.setGameOver(false);
        gameUI.updateUIState();
    }
}
