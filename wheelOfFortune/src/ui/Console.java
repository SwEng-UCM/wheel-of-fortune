package ui;

import players.Player;
import utils.InputHelper;
import java.util.ArrayList;
import java.util.List;

public class Console {
    public static void showMessage(String message) {
        System.out.println(message);
    }

    public static void showMessageInLine(String message) {
        System.out.print(message);
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void showBanner() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("   🎡 WHEEL OF FORTUNE GAME 🎡   ");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    public static List<Player> registerPlayers() {
        List<Player> players = new ArrayList<>();
        int numPlayers = InputHelper.getInteger("\n👥 Enter the number of players (minimum 2): ", 2);

        for (int i = 0; i < numPlayers; i++) {
            String name = InputHelper.getText("📝 Enter the name of Player " + (i + 1) + ": ");
            players.add(new Player(name));
        }
        return players;
    }

    public static int pannelCounter(String frase) {
        int letters = 0;

        for (int i = 0; i < frase.length(); i++) {
            if (frase.charAt(i) != ' ') {
                letters++;
            }
        }

        return letters;
    }
}

