package main;

import ui.GameUI;

import javax.swing.SwingUtilities;

import game.Game;

public class Main {
    public static void main(String[] args) {
    	SwingUtilities.invokeLater(() -> {
    	    Game.getInstance(null); // Se crea la instancia del juego (si no existe)
    	    new GameUI();
    	});
    }
}
