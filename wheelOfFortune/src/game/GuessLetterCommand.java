package game;

import players.Player;
import ui.GameUI;

public class GuessLetterCommand implements Command {
    private GameUI gameUI;
    private char guessedLetter;
    private char[] previousRevealed;
    private int currentPlayerIndex;
    private int previousMoney;
    private boolean letterWasUsedBefore;


    public GuessLetterCommand(GameUI gameUI, char guessedLetter) {
        this.gameUI = gameUI;
        this.guessedLetter = guessedLetter;
    }

    @Override
    public void execute() {
    	letterWasUsedBefore = gameUI.getUsedLettersPanel().isLetterUsed(guessedLetter);
    	
        Game game = gameUI.getGame();
        currentPlayerIndex = game.getCurrentPlayerIndex();
        Player currentPlayer = game.getPlayers().get(currentPlayerIndex);
        previousRevealed = gameUI.getRevealed().clone();
        previousMoney = currentPlayer.getMoney();

        gameUI.guessLetter(String.valueOf(guessedLetter));
    }
    
@Override
public void undo() {
    Game game = gameUI.getGame();
    Player currentPlayer = game.getPlayers().get(currentPlayerIndex);

    // Restaurar estado anterior
    currentPlayer.addMoney(previousMoney - currentPlayer.getMoney());

    // Restaurar la frase revelada
    game.setRevealed(previousRevealed.clone());
    gameUI.synchronizeRevealed(); // ✅ Sincroniza visual con lógica

    gameUI.setGameOver(false);

    // Quitar la letra si no estaba usada antes
    if (!letterWasUsedBefore) {
        gameUI.getUsedLettersPanel().removeLetter(guessedLetter);
    }

    gameUI.updateUIState(); // Refresca los paneles visuales
}
}
