package model.memento;

import model.state.GameState;

public class GameStateMemento {
    private final GameState state;

    public GameStateMemento(GameState state) {
        this.state = state;
    }

    public GameState getSavedState() {
        return state;
    }
}
