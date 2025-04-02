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
    private int messageCount;


    public SpinCommand(GameUI gameUI) {
        this.gameUI = gameUI;
    }


    @Override
    public void execute() {
        // Guardar el texto completo antes de realizar el giro
        int initialMessageCount = gameUI.getBottomPanel().getMessageCount();
        messageCount = initialMessageCount;

        Game game = gameUI.getGame();
        currentPlayerIndex = game.getCurrentPlayerIndex();
        Player currentPlayer = game.getPlayers().get(currentPlayerIndex);

        previousMoney = currentPlayer.getMoney();
        previousX2Active = gameUI.isX2Active();
        previousHasSpun = gameUI.hasSpun();
        previousSpinValue = gameUI.getCurrentSpinValue();

        // Realizar el giro
        gameUI.spinWheel();

        // Calcular el número de mensajes generados
        int finalMessageCount = gameUI.getBottomPanel().getMessageCount();
        messageCount = finalMessageCount - initialMessageCount;
    }

    @Override
    public void undo() {
        Game game = gameUI.getGame();
        Player currentPlayer = game.getPlayers().get(currentPlayerIndex);

        // Restaurar el estado del jugador
        currentPlayer.addMoney(previousMoney - currentPlayer.getMoney());
        gameUI.setHasSpun(previousHasSpun);
        gameUI.setX2Active(previousX2Active);
        gameUI.setCurrentSpinValue(previousSpinValue);
        game.setCurrentPlayerIndex(currentPlayerIndex);

        // Eliminar todos los mensajes generados por el último giro
        clearAllMessages(gameUI, messageCount);

        gameUI.updateUIState();
        gameUI.getCenterPanel().refreshButtons();
    }



}
