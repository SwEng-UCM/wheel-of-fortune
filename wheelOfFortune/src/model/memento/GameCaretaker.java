package model.memento;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.state.GameState;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GameCaretaker {

    private static final String SAVE_PATH = "saved_game.json";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void save(GameStateMemento memento) {
        try (FileWriter writer = new FileWriter(SAVE_PATH)) {
            gson.toJson(memento.getSavedState(), writer);
            System.out.println("Game saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GameStateMemento load() {
        try (FileReader reader = new FileReader(SAVE_PATH)) {
            GameState state = gson.fromJson(reader, GameState.class);
            System.out.println("Game loaded.");
            return new GameStateMemento(state);
        } catch (IOException e) {
            System.out.println("No saved game found.");
            return null;
        }
    }
}
