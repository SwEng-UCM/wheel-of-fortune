package game;

import players.Player;
import ui.GameUI;

public class BuyVowelCommand implements Command {
    private GameUI gameUI;
    private char vowel;
    private char[] previousRevealed;
    private int currentPlayerIndex;
    private int previousMoney;
    private int messageCount;


    public BuyVowelCommand(GameUI gameUI, char vowel) {
        this.gameUI = gameUI;
        this.vowel = vowel;
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

        // Comprar la vocal
        gameUI.buyVowel(String.valueOf(vowel));
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

        gameUI.getUsedLettersPanel().removeLetter(vowel);

        clearAllMessages(gameUI, messageCount);
        
        gameUI.refreshPlayerCards();
        if (GameUI.serverInstance != null) {
            GameUI.serverInstance.broadcastGameState(gameUI.getGame());
        }

        gameUI.updateUIState();
    }



}
