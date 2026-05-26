package model;
import java.awt.Color;

public abstract class Word {
    private String text;
    private int x, y;
    private int speed;
    private Color color;

    public Word(String text, int x, int speed, Color color) {
        this.text = text;
        this.x = x;
        this.y = 30; // Mulai dari atas layar
        this.speed = speed;
        this.color = color;
    }

    public void moveDown() {
        this.y += speed;
    }

    // point
    public abstract int onTypeSuccess();

    public String getText() { return text; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getSpeed() { return speed; }
    public Color getColor() { return color; }
}
