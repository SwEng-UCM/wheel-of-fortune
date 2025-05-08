package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.GameUI;
import players.Player;
import game.Game;
import javax.swing.SwingUtilities;

public class GameUITest {
    private static GameUI gameUI;
    private static Game game;
    private static boolean initialized = false;

    @BeforeEach
    public void setup() throws Exception {
        if (!initialized) {
            SwingUtilities.invokeAndWait(() -> {
                gameUI = new GameUI();
            });
            game = gameUI.getGame();
            
            Player player1 = new Player("Player 1");
            player1.addMoney(200); // Iniciar con $200 para asegurar que puede comprar vocales
            Player player2 = new Player("Player 2");
            player2.addMoney(50); // Este jugador no podrá comprar vocales ($50 < $75)
            
            game.addPlayer(player1);
            game.addPlayer(player2);
            game.setPhrase("HELLO WORLD");
            gameUI.updateUIState();
            initialized = true;
        }
    }

    @Test
    public void testSpinWheel() {
        gameUI.spinWheel();
        int spinValue = gameUI.getCurrentSpinValue();
        assertTrue(spinValue >= 0, "El valor del spin debe ser mayor o igual a 0");
        assertFalse(gameUI.isGameOver(), "El juego no debe terminar después de un spin normal");
    }

    @Test
    public void testGuessLetter() {
        gameUI.setHasSpun(true);
        boolean result = gameUI.guessLetter("H");
        assertTrue(result, "La letra 'H' debería estar en la frase");
        assertFalse(gameUI.isGameOver(), "El juego no debe terminar después de adivinar una letra");
    }

    @Test
    public void testBuyVowelWithMoney() {
        Player player = game.getPlayers().get(0); // Jugador con $200
        int initialMoney = player.getMoney();

        if (initialMoney >= 75) {
            gameUI.buyVowel("O");
            assertEquals(initialMoney - 75, player.getMoney(), "Se debe restar $75 al jugador después de comprar una vocal");
        } else {
            gameUI.buyVowel("O");
            assertEquals(initialMoney, player.getMoney(), "El saldo no debe cambiar si el jugador no tiene suficiente dinero");
        }

        assertFalse(gameUI.isGameOver(), "El juego no debe terminar después de intentar comprar una vocal");
    }

    @Test
    public void testBuyVowelWithoutMoney() {
        Player player = game.getPlayers().get(1); // Jugador con $50
        int initialMoney = player.getMoney();

        if (initialMoney >= 75) {
            gameUI.buyVowel("O");
            assertEquals(initialMoney - 75, player.getMoney(), "Se debe restar $75 al jugador después de comprar una vocal");
        } else {
            gameUI.buyVowel("O");
            assertEquals(initialMoney, player.getMoney(), "El saldo no debe cambiar si el jugador no tiene suficiente dinero");
        }

        assertFalse(gameUI.isGameOver(), "El juego no debe terminar después de intentar comprar una vocal");
    }
}
