package model.memento;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.state.GameState;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GameCaretaker {

    private final String savePath;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /** Constructores: por defecto usa saved_game.json, o bien uno personalizado */
    public GameCaretaker() {
        this("saved_game.json");
    }
    public GameCaretaker(String savePath) {
        this.savePath = savePath;
    }

    public void save(GameStateMemento memento) {
        try (FileWriter writer = new FileWriter(savePath)) {
            gson.toJson(memento.getSavedState(), writer);
            System.out.println("Game saved to " + savePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GameStateMemento load() {
        try (FileReader reader = new FileReader(savePath)) {
            GameState state = gson.fromJson(reader, GameState.class);
            System.out.println("Game loaded from " + savePath);
            return new GameStateMemento(state);
        } catch (IOException e) {
            System.out.println("No saved game found at " + savePath);
            return null;
        }
    }
}
