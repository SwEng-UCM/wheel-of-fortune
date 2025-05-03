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

    public GameClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        try {
            Socket socket = new Socket(host, port);
            MessageReceiver receiver = new MessageReceiver(socket);
            System.out.println("[Client] Connected to server at " + host + ":" + port);

            try {
                SwingUtilities.invokeAndWait(() -> {
                    gameUI = new GameUI(true);
                    gameUI.setClientInfo(host, port);
                });
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error initializing client UI");
                return;
            }


            // Añadimos temporizador de espera de estado
            Timer timeout = new Timer();
            timeout.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (gameUI.getGame().getPlayers().isEmpty()) {
                        System.out.println("[Client] Still no game state after 3s... sending request ping.");
                        // Aquí podrías implementar un sistema de ping si tuvieras otro canal de comunicación.
                        // Por ahora solo lo mostramos.
                    }
                }
            }, 3000); // 3 segundos

            // Bucle principal de recepción
            while (true) { 	
                Object obj = receiver.receive();
                if (obj instanceof NetworkMessage message) {
                    GameState state = message.getGameState();
                    SwingUtilities.invokeLater(() -> {
                        System.out.println("[Client] Recibido estado del servidor.");
                        gameUI.applyRemoteGameState(state);
                    });
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "Connection lost to server.");
            System.exit(1);
        }
    }

}
