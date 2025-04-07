package model.state;

public class PlayerState {
    private String name;
    private String avatarFileName;
    private int money;

    public PlayerState() {}

    public PlayerState(String name, String avatarFileName, int money) {
        this.name = name;
        this.avatarFileName = avatarFileName;
        this.money = money;
    }

    public String getName() { return name; }
    public String getAvatar() { return avatarFileName; }
    public int getMoney() { return money; }

    public void setName(String name) { this.name = name; }
    public void setAvatar(String avatarFileName) { this.avatarFileName = avatarFileName; }
    public void setMoney(int money) { this.money = money; }
}
