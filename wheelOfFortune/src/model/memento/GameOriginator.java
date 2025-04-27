package model.memento;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.state.GameState;

public class GameOriginator {
    private GameState currentState;
    private final Gson gson = new GsonBuilder().create();

    public void setState(GameState state) {
        this.currentState = state;
    }

    public GameState getCurrentState() {
        return currentState;
    }

    /** Crea un memento con una copia profunda de currentState */
    public GameStateMemento saveToMemento() {
        // serializamos → cadena JSON → deserializamos para obtener copia independiente
        String json = gson.toJson(currentState);
        GameState copy = gson.fromJson(json, GameState.class);
        return new GameStateMemento(copy);
    }

    public void restoreFromMemento(GameStateMemento memento) {
        this.currentState = memento.getSavedState();
    }
}
