package network;

import model.state.GameState;
import java.io.Serializable;

public class NetworkMessage implements Serializable {
    private GameState gameState;

    public NetworkMessage(GameState gameState) {
        this.gameState = gameState;
    }

    public GameState getGameState() {
        return gameState;
    }
}
