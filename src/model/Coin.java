package model;

import java.awt.Color;
import java.awt.Graphics2D;
import utils.Constants;

public class Coin extends Resource {

    public Coin(int x, int y) {
        super(x, y, Constants.COLOR_COIN, 5); // 5 points or used as currency
    }

    @Override
    public void draw(Graphics2D g, int tileSize) {
        g.setColor(color);
        g.fillOval(x * tileSize, y * tileSize, tileSize, tileSize);
        g.setColor(Color.WHITE);
        g.drawString("$", x * tileSize + 8, y * tileSize + 18);
    }
}
