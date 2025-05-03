package network;

import game.Game;
import model.state.GameState;
import ui.GameUI;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import javax.swing.SwingUtilities;

public class GameServer {
    private final int port;
    private final List<MessageSender> clients = new ArrayList<>();
    private ServerSocket serverSocket;

    public GameServer(int port) {
        this.port = port;
    }

    public void start() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port); // ✅ esto es lo que faltaba
                System.out.println("[Server] Listening on port " + port);

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("[Server] Client connected: " + clientSocket);

                    try {
                        MessageSender sender = new MessageSender(clientSocket);
                        synchronized (clients) {
                            clients.add(sender);
                            if (gameUI != null) {
                                SwingUtilities.invokeLater(() ->
                                    gameUI.updateClientStatusLabel(clients.size())
                                );
                            }

                        }

                        // Enviar estado si ya está listo
                        sendStateToClient(sender);

                    } catch (Exception e) {
                        System.out.println("❌ Error initializing client: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

            } catch (IOException e) {
                System.out.println("❌ Error starting server: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }


    public void broadcastGameState(Game game) {
        GameState state = game.createGameState();
        if (state == null) {
            System.out.println("⛔ No se envió GameState porque aún no está inicializado.");
            return;
        }

        NetworkMessage msg = new NetworkMessage(state);
        synchronized (clients) {
            Iterator<MessageSender> iterator = clients.iterator();
            while (iterator.hasNext()) {
                MessageSender sender = iterator.next();
                try {
                    sender.send(msg);
                } catch (Exception e) {
                    System.out.println("[Server] Removing disconnected client.");
                    iterator.remove();
                }
            }
        }
    }


    public int getClientCount() {
        synchronized (clients) {
            return clients.size();
        }
    }
    
    public void sendStateToClient(MessageSender sender) {
        GameState state = Game.getInstance(null).createGameState();
        if (state == null) {
            System.out.println("⚠️ No se puede enviar GameState: el juego aún no está inicializado.");
            return;
        }

        try {
            sender.send(new NetworkMessage(state));
            System.out.println("[Server] Estado enviado a nuevo cliente.");
        } catch (Exception e) {
            System.out.println("❌ Error enviando estado al cliente: " + e.getMessage());
        }
    }
    
 // GameServer.java
    private GameUI gameUI;

    public GameServer(int port, GameUI gameUI) {
        this.port = port;
        this.gameUI = gameUI;
    }


}

