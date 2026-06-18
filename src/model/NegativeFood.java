package model;

import java.awt.Graphics2D;
import utils.Constants;

public class NegativeFood extends Resource {

    public NegativeFood(int x, int y) {
        super(x, y, Constants.COLOR_FOOD_NEG, -1);
    }

    @Override
    public void draw(Graphics2D g, int tileSize) {
        g.setColor(color);
        int padding = 4;
        g.fillRect(x * tileSize + padding, y * tileSize + padding, tileSize - 2 * padding, tileSize - 2 * padding);
    }
}
