package network;

import model.state.GameState;
import ui.GameUI;

import javax.swing.*;
import java.net.Socket;
import java.util.TimerTask;
import java.util.Timer;

public class GameClient {
    private final String host;
    private final int port;
    private GameUI gameUI;
    private MessageSender sender;
    

    public GameClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        try {
            Socket socket = new Socket(host, port);
            this.sender = new MessageSender(socket);
            MessageReceiver receiver = new MessageReceiver(socket);
            System.out.println("[Client] Connected to server at " + host + ":" + port);

            try {
                SwingUtilities.invokeAndWait(() -> {
                    gameUI = new GameUI(true);
                    gameUI.setClient(this);
                    gameUI.setClientInfo(host, port);
                });
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error initializing client UI");
                return;
            }

            // Bucle principal de recepción
            while (true) {     
                Object obj = receiver.receive();
                if (obj instanceof NetworkMessage message) {
                    if (message.isGameState()) {
                        GameState state = message.getGameState();
                        SwingUtilities.invokeLater(() -> {
                            System.out.println("[Client] Recibido estado del servidor.");
                            gameUI.applyRemoteGameState(state);
                        });
                    } else if (message.isChatMessage()) {
                        ChatMessage chatMsg = message.getChatMessage();
                        SwingUtilities.invokeLater(() -> {
                            gameUI.displayChatMessage(chatMsg);
                        });
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Connection lost to server.");
            System.exit(1);
        }
    }

    public void sendChatMessage(String message) {
        try {
            ChatMessage chatMsg = new ChatMessage(message, false);
            sender.send(new NetworkMessage(chatMsg));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
}
