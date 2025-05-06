package network;

import java.io.Serializable;
import java.awt.Color;

public class ChatMessage implements Serializable {
    private final String message;
    private final boolean isHost;
    private final transient Color color; // Transient para no serializar

    public ChatMessage(String message, boolean isHost) {
        this.message = message;
        this.isHost = isHost;
        this.color = isHost ? new Color(0, 100, 0) : new Color(0, 0, 139); // Verde oscuro para host, azul oscuro para client
    }

    public String getMessage() {
        return message;
    }

    public boolean isHost() {
        return isHost;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return isHost ? "[Host]: " + message : "[Client]: " + message;
    }
}