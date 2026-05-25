package model;

public class Player {
    private String username;
    private int score;
    private int hp;
    private int level;

    public Player(String username) {
        this.username = username;
        this.score = 0;
        this.hp = 3; // 3 Nyawa
        this.level = 1;
    }

    public void reduceHp() { this.hp--; }
    public void addScore(int points) { this.score += points; }

    // Getter & Setter
    public String getUsername() { return username; }
    public int getScore() { return score; }
    public int getHp() { return hp; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
}
