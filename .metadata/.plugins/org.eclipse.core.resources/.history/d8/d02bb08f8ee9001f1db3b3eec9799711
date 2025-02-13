package game;

import players.Player;
import ui.Console;
import java.util.ArrayList;
import java.util.List;

public class Game {
    private List<Player> players;

    public Game() {
        players = new ArrayList<>();
    }

    public void start() {
        Console.showMessage("Welcome to the Wheel of Fortune game!");
        players = Console.registerPlayers();
        Console.showMessage("Players registered successfully!");
    }
}