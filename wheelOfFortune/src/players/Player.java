package players;

import javax.swing.ImageIcon;

public class Player {
    private String name;
    private int score;
    private int money;
    private ImageIcon avatar;
    private String avatarFileName; // ← NUEVO

    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.money = 0;
    }

    // NUEVO: Constructor usado al cargar partida
    public Player(String name, String avatarFileName, int money) {
        this.name = name;
        this.avatarFileName = avatarFileName;
        this.avatar = new ImageIcon("resources/" + avatarFileName);
        this.money = money;
        this.score = 0;
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

    public ImageIcon getAvatar() {
        return avatar;
    }

    // MODIFICADO
    public void setAvatar(ImageIcon avatar, String avatarFileName) {
        this.avatar = avatar;
        this.avatarFileName = avatarFileName;
    }

    // NUEVO
    public String getAvatarKey() {
        return avatarFileName;
    }
}
