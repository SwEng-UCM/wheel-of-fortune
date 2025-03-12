package main;

import ui.GameUI;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GameUI(); // 🔹 Iniciar el juego desde GameUI
        });
    }
}
