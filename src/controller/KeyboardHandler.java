package controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import model.Direction;
import model.Snake;

public class KeyboardHandler extends KeyAdapter {
    private Snake snake1;
    private Snake snake2;
    private boolean isMultiplayer;
    private GameController gameController;

    public KeyboardHandler(GameController gameController) {
        this.gameController = gameController;
    }

    public void setSnakes(Snake snake1, Snake snake2, boolean isMultiplayer) {
        this.snake1 = snake1;
        this.snake2 = snake2;
        this.isMultiplayer = isMultiplayer;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // Game Controls
        if (key == KeyEvent.VK_ESCAPE) {
            if (gameController.isGameOver()) {
                gameController.backToMenu();
            } else {
                gameController.pauseOrResume();
            }
        }

        // Snake 1 Controls (Arrow Keys)
        if (snake1 != null && snake1.isAlive()) {
            if (key == KeyEvent.VK_UP) {
                snake1.setDirection(Direction.UP);
            } else if (key == KeyEvent.VK_DOWN) {
                snake1.setDirection(Direction.DOWN);
            } else if (key == KeyEvent.VK_LEFT) {
                snake1.setDirection(Direction.LEFT);
            } else if (key == KeyEvent.VK_RIGHT) {
                snake1.setDirection(Direction.RIGHT);
            }
        }

        // Snake 2 Controls (WASD / ZQSD)
        if (isMultiplayer && snake2 != null && snake2.isAlive()) {
            if (key == KeyEvent.VK_W || key == KeyEvent.VK_Z) {
                snake2.setDirection(Direction.UP);
            } else if (key == KeyEvent.VK_S) {
                snake2.setDirection(Direction.DOWN);
            } else if (key == KeyEvent.VK_A || key == KeyEvent.VK_Q) {
                snake2.setDirection(Direction.LEFT);
            } else if (key == KeyEvent.VK_D) {
                snake2.setDirection(Direction.RIGHT);
            }
        }
    }
}
