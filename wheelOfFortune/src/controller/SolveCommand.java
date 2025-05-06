package controller;
import players.Player;
import ui.GameUI;
import game.Game;

public class SolveCommand implements Command {
    private GameUI gameUI;
    private String attempt;
    private char[] previousRevealed;
    private boolean wasGameOverBefore;
    private int currentPlayerIndex;
    private int messageCount;


    public SolveCommand(GameUI gameUI, String attempt) {
        this.gameUI = gameUI;
        this.attempt = attempt;
    }

    
    @Override
    public void execute() {
        int initialMessageCount = gameUI.getBottomPanel().getMessageCount();
        messageCount = initialMessageCount;

        Game game = gameUI.getGame();
        currentPlayerIndex = game.getCurrentPlayerIndex();
        previousRevealed = gameUI.getRevealed().clone();
        wasGameOverBefore = gameUI.isGameOver();

        gameUI.attemptSolve(attempt);
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

        game.setRevealed(previousRevealed.clone());
        gameUI.synchronizeRevealed();
        gameUI.setGameOver(wasGameOverBefore);
        game.setCurrentPlayerIndex(currentPlayerIndex);

        clearAllMessages(gameUI, messageCount);
        
        gameUI.refreshPlayerCards();
        if (GameUI.serverInstance != null) {
            GameUI.serverInstance.broadcastGameState(gameUI.getGame());
        }

        gameUI.updateUIState();
    }



}