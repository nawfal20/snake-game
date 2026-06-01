package model;

import java.awt.Color;
import java.awt.Graphics2D;

public abstract class Resource {
    protected int x;
    protected int y;
    protected Color color;
    protected int value;

    public Resource(int x, int y, Color color, int value) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.value = value;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getValue() {
        return value;
    }

    public abstract void draw(Graphics2D g, int tileSize);
}
