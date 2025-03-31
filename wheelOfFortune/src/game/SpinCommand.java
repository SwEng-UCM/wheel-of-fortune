package game;

import players.Player;
import ui.GameUI;

public class SpinCommand implements Command {
    private GameUI gameUI;
    private int previousMoney;
    private int currentPlayerIndex;
    private boolean previousX2Active;
    private boolean previousHasSpun;
    private int previousSpinValue;

    public SpinCommand(GameUI gameUI) {
        this.gameUI = gameUI;
    }

    @Override
    public void execute() {
        Game game = gameUI.getGame();
        currentPlayerIndex = game.getCurrentPlayerIndex();
        Player currentPlayer = game.getPlayers().get(currentPlayerIndex);

        previousMoney = currentPlayer.getMoney();
        previousX2Active = gameUI.isX2Active();
        previousHasSpun = gameUI.hasSpun();
        previousSpinValue = gameUI.getCurrentSpinValue();

        gameUI.spinWheel();
    }

@Override
public void undo() {
    Game game = gameUI.getGame();
    Player currentPlayer = game.getPlayers().get(currentPlayerIndex);

    // Restaurar el dinero
    currentPlayer.addMoney(previousMoney - currentPlayer.getMoney());

    // Restaurar el estado del giro y X2
    gameUI.setHasSpun(previousHasSpun);
    gameUI.setX2Active(previousX2Active);
    gameUI.setCurrentSpinValue(previousSpinValue);

    // Restaurar el turno si se perdió
    game.setCurrentPlayerIndex(currentPlayerIndex);

    // Actualizar el estado de los botones
    gameUI.updateUIState();
    gameUI.getBottomPanel().appendMessage("↩️ Undo performed: Spin reversed.");
    gameUI.getCenterPanel().refreshButtons();  // ✅ Actualizar el estado del botón Spin
}
}
