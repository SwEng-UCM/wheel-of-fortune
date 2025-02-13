package game;

import players.Player;
import ui.Console;
import java.util.ArrayList;
import java.util.List;

public class Game {
    private List<Player> players;

    private int NumWords;

    public Game() {
        players = new ArrayList<>();
        NumWords = 0;
    }

    public void start() {
        Console.showMessage("Welcome to the Wheel of Fortune game!");
        players = Console.registerPlayers();
        Console.showMessage("Players registered successfully!");
    }

    public void pannel(String frase) {
    	NumWords = Console.pannelCounter(frase);
    	Console.showMessage("This pannel contains " + NumWords + " words");
    	for(int i = 0; i < NumWords; i++) {
    		if(frase.charAt(i) != ' ') {
    			Console.showMessageInLine("|_|");
    		} else {
    			Console.showMessageInLine(" ");
    		}
    	}
    }
    
    public void spinWheel() {
    	Console.showMessage("Spin the wheel!!!");
    }
 
}