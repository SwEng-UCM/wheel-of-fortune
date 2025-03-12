package players;

import javax.swing.ImageIcon;

public class Player {
    private String name;
    private int score;
    private int money; // Nueva variable para almacenar el dinero ganado
    private ImageIcon avatar; // Nuevo atributo para almacenar el avatar

    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.money = 0; // Inicialmente, el dinero es 0
        
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int points) {
        this.score += points;
    }

    public int getMoney() {
        return money;
    }

    public void addMoney(int amount) {
        this.money += amount;
    }
    
    public int getBudget() {
        return money;
    }

    // Agrega los métodos getter y setter para el avatar:
    public ImageIcon getAvatar() {
        return avatar;
    }

    public void setAvatar(ImageIcon avatar) {
        this.avatar = avatar;
    }
}
