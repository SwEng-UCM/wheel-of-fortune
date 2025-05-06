package controller;


import players.Player;
import game.Game;
import ui.GameUI;

public class GuessLetterCommand implements Command {
    private GameUI gameUI;
    private char guessedLetter;
    private char[] previousRevealed;
    private int currentPlayerIndex;
    private int previousMoney;
    private boolean letterWasUsedBefore;
    private int messageCount;



    public GuessLetterCommand(GameUI gameUI, char guessedLetter) {
        this.gameUI = gameUI;
        this.guessedLetter = guessedLetter;
    }


    @Override
    public void execute() {
        int initialMessageCount = gameUI.getBottomPanel().getMessageCount();
        messageCount = initialMessageCount;

        Game game = gameUI.getGame();
        currentPlayerIndex = game.getCurrentPlayerIndex();
        Player currentPlayer = game.getPlayers().get(currentPlayerIndex);
        previousRevealed = gameUI.getRevealed().clone();
        previousMoney = currentPlayer.getMoney();

        // Adivinar la letra
        gameUI.guessLetter(String.valueOf(guessedLetter));
        gameUI.refreshPlayerCards();
        if (GameUI.serverInstance != null) {
            GameUI.serverInstance.broadcastGameState(gameUI.getGame());
        }


        int finalMessageCount = gameUI.getBottomPanel().getMessageCount();
        messageCount = finalMessageCount - initialMessageCount;
    }

    @Override
    public void undo() {
        Game game = gameUI.getGame();
        Player currentPlayer = game.getPlayers().get(currentPlayerIndex);

        currentPlayer.addMoney(previousMoney - currentPlayer.getMoney());
        game.setRevealed(previousRevealed.clone());
        gameUI.synchronizeRevealed();

        if (!letterWasUsedBefore) {
            gameUI.getUsedLettersPanel().removeLetter(guessedLetter);
        }

        clearAllMessages(gameUI, messageCount);
        
        gameUI.refreshPlayerCards();
        if (GameUI.serverInstance != null) {
            GameUI.serverInstance.broadcastGameState(gameUI.getGame());
        }

        gameUI.updateUIState();
    }


}
