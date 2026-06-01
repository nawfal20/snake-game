package model;

import java.awt.Color;
import java.awt.Graphics2D;
import utils.Constants;

public class PositiveFood extends Resource {

    public PositiveFood(int x, int y) {
        super(x, y, Constants.COLOR_FOOD_POS, 1);
    }

    @Override
    public void draw(Graphics2D g, int tileSize) {
        g.setColor(color);
        g.fillOval(x * tileSize, y * tileSize, tileSize, tileSize);
    }
}
