package game;

import ui.GameUI;

public interface Command {
    void execute();
    void undo();
    
    // Método para limpiar todos los mensajes generados por el comando
    default void clearAllMessages(GameUI gameUI, int numMessages) {
        gameUI.getBottomPanel().clearLastMessages(numMessages);
    }
}
