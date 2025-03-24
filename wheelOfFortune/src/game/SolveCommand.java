package game;

import players.Player;
import ui.GameUI;

public class SolveCommand implements Command {
    private GameUI gameUI;
    private String attempt;
    private char[] previousRevealed;
    private boolean wasGameOverBefore;
    private int currentPlayerIndex;

    public SolveCommand(GameUI gameUI, String attempt) {
        this.gameUI = gameUI;
        this.attempt = attempt;
    }

    @Override
    public void execute() {
        Game game = gameUI.getGame();
        currentPlayerIndex = game.getCurrentPlayerIndex();
        previousRevealed = gameUI.getRevealed().clone();
        wasGameOverBefore = gameUI.isGameOver();

        gameUI.attemptSolve(attempt);
    }

    @Override
    public void undo() {
        Game game = gameUI.getGame();

        // Restaurar el estado revelado y turno
        game.setRevealed(previousRevealed.clone());
        gameUI.synchronizeRevealed();
        gameUI.setGameOver(wasGameOverBefore);

        // Restaurar turno al jugador anterior si fue penalizado por fallar
        game.setCurrentPlayerIndex(currentPlayerIndex);

        gameUI.updateUIState();
    }
}
