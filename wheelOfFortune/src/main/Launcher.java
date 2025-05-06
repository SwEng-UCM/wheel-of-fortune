package main;

import network.GameClient;
import network.GameServer;
import ui.GameUI;

import javax.swing.*;

public class Launcher {
    public static void main(String[] args) {
        String[] options = {"Host Game", "Join Game"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "Select multiplayer mode:",
                "Wheel of Fortune Launcher",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) {
            // Host
        	SwingUtilities.invokeLater(() -> {
        	    GameUI ui = new GameUI(); // Crea la UI primero
        	    GameServer server = new GameServer(5000, ui); // Le pasas la UI
        	    GameUI.serverInstance = server;
        	    server.start();
        	});


        } else if (choice == 1) {
            // Client
            String ip = JOptionPane.showInputDialog("Enter Host IP address:", "localhost");
            if (ip != null && !ip.trim().isEmpty()) {
                GameClient client = new GameClient(ip.trim(), 5000);
                client.start();
            }
        }
    }
}