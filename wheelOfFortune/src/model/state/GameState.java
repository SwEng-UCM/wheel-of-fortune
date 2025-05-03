package model.state;

import java.util.List;
import java.io.Serializable;

public class GameState implements Serializable{
    private List<PlayerState> players;
    private String wordToGuess;
    private List<Character> revealedLetters;
    private List<Character> usedLetters;
    private int currentPlayerIndex;

    public GameState() {} // Necesario para Gson

    public GameState(List<PlayerState> players, String wordToGuess,
                     List<Character> revealedLetters, List<Character> usedLetters,
                     int currentPlayerIndex) {
        this.players = players;
        this.wordToGuess = wordToGuess;
        this.revealedLetters = revealedLetters;
        this.usedLetters = usedLetters;
        this.currentPlayerIndex = currentPlayerIndex;
    }

    public List<PlayerState> getPlayers() { return players; }
    public String getWordToGuess() { return wordToGuess; }
    public List<Character> getRevealedLetters() { return revealedLetters; }
    public List<Character> getUsedLetters() { return usedLetters; }
    public int getCurrentPlayerIndex() { return currentPlayerIndex; }

    // Setters
    public void setPlayers(List<PlayerState> players) { this.players = players; }
    public void setWordToGuess(String wordToGuess) { this.wordToGuess = wordToGuess; }
    public void setRevealedLetters(List<Character> revealedLetters) { this.revealedLetters = revealedLetters; }
    public void setUsedLetters(List<Character> usedLetters) { this.usedLetters = usedLetters; }
    public void setCurrentPlayerIndex(int currentPlayerIndex) { this.currentPlayerIndex = currentPlayerIndex; }
}
