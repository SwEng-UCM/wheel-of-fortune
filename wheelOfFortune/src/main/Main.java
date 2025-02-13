package main;

import game.Game;

public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        game.start();
        game.pannel("El Madrid ha ganado al City");
        game.spinWheel();
    }
}