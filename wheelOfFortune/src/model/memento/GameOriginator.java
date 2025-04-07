package model.memento;

import model.state.GameState;

public class GameOriginator {
    private GameState currentState;

    public void setState(GameState state) {
        this.currentState = state;
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public GameStateMemento saveToMemento() {
        return new GameStateMemento(currentState);
    }

    public void restoreFromMemento(GameStateMemento memento) {
        this.currentState = memento.getSavedState();
    }
}
