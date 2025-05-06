package network;

import model.state.GameState;
import java.io.Serializable;

public class NetworkMessage implements Serializable {
    private GameState gameState;
    private ChatMessage chatMessage;

    public NetworkMessage(GameState gameState) {
        this.gameState = gameState;
    }

    public NetworkMessage(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }

    public GameState getGameState() {
        return gameState;
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    public boolean isGameState() {
        return gameState != null;
    }

    public boolean isChatMessage() {
        return chatMessage != null;
    }
}

