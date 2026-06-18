package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameBoard {
    private int width;
    private int height;
    private List<Resource> resources;
    private Random random;

    public GameBoard(int width, int height) {
        this.width = width;
        this.height = height;
        this.resources = new ArrayList<>();
        this.random = new Random();
    }

    public void generateResource(Snake snake1, Snake snake2) {
        boolean hasCoin = false;
        for (Resource r : resources) {
            if (r instanceof Coin) {
                hasCoin = true;
                break;
            }
        }

        int type;
        if (!hasCoin) {
            type = 8; // Force coin spawn
        } else {
            type = random.nextInt(10); // 0-5 Positive, 6-7 Negative, 8-9 Coin
        }
        int x, y;
        boolean validPosition;

        do {
            validPosition = true;
            x = random.nextInt(width);
            y = random.nextInt(height);

            // Check collision with snake 1
            if (snake1 != null) {
                for (Segment s : snake1.getBody()) {
                    if (s.getX() == x && s.getY() == y)
                        validPosition = false;
                }
            }

            // Check collision with snake 2
            if (snake2 != null) {
                for (Segment s : snake2.getBody()) {
                    if (s.getX() == x && s.getY() == y)
                        validPosition = false;
                }
            }

            // Check collision with other resources
            for (Resource r : resources) {
                if (r.getX() == x && r.getY() == y)
                    validPosition = false;
            }

        } while (!validPosition);

        if (type < 6) {
            resources.add(new PositiveFood(x, y));
        } else if (type < 8) {
            resources.add(new NegativeFood(x, y));
        } else {
            resources.add(new Coin(x, y));
        }
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void removeResource(Resource resource) {
        resources.remove(resource);
    }
}
