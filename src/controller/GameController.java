package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;
import model.*;
import utils.Constants;
import view.GamePanel;
import view.MainFrame;

public class GameController implements ActionListener {
    private MainFrame mainFrame;
    private GamePanel gamePanel;
    private GameBoard gameBoard;
    private Snake snake1;
    private Snake snake2;
    private boolean isMultiplayer;

    private Timer timer;
    private boolean isPaused;
    private boolean isGameOver;

    private int score1;
    private int score2;
    private int coinsCollected;

    private long startTime;
    private int level;
    private int levelUpFlashTicks = 0;

    private Player activePlayer;
    private String player2Name = "Player 2";
    private KeyboardHandler keyboardHandler;

    // === EFFECTS ===
    private int speedBoostTicks = 0;
    private int coinMagnetTicks = 0;
    private int invincibilityTicks = 0;
    private int baseDelay;
    private List<String> pendingEffects = new ArrayList<>();

    public GameController() {
        this.keyboardHandler = new KeyboardHandler(this);
        this.timer = new Timer(Constants.GAME_SPEED_NORMAL, this);
        this.baseDelay = Constants.GAME_SPEED_NORMAL;
    }

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public void backToMenu() {
        if (mainFrame != null) {
            mainFrame.showPanel("MENU");
        }
    }

    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public KeyboardHandler getKeyboardHandler() {
        return keyboardHandler;
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(Player player) {
        this.activePlayer = player;
    }

    public int getLevelUpFlashTicks() {
        return levelUpFlashTicks;
    }

    // === EFFECT METHODS ===
    public void activateEffect(String effectName) {
        if (effectName == null)
            return;

        // If game is not running, queue the effect for next game start
        if (isGameOver || gameBoard == null || !timer.isRunning()) {
            pendingEffects.add(effectName);
            return;
        }

        // Apply effect immediately if game is running
        applyEffectNow(effectName);
    }

    private void applyEffectNow(String effectName) {
        String name = effectName.toLowerCase();
        if (name.contains("speed")) {
            // Speed Boost: ~10 seconds worth of ticks
            speedBoostTicks = (int) (10000.0 / timer.getDelay());
            applySpeedBoost(true);
        } else if (name.contains("magnet") || name.contains("coin")) {
            // Coin Magnet: ~30 seconds worth of ticks
            coinMagnetTicks = (int) (30000.0 / timer.getDelay());
        } else if (name.contains("invincib")) {
            // Invincibility: ~30 seconds worth of ticks
            invincibilityTicks = (int) (30000.0 / timer.getDelay());
        }
    }

    public boolean hasPendingEffects() {
        return !pendingEffects.isEmpty();
    }

    private void applySpeedBoost(boolean boost) {
        if (boost) {
            int boostedDelay = (int) (baseDelay * 0.65); // 35% faster
            timer.setDelay(Math.max(20, boostedDelay));
        } else {
            timer.setDelay(baseDelay);
        }
    }

    public boolean isSpeedBoostActive() {
        return speedBoostTicks > 0;
    }

    public boolean isCoinMagnetActive() {
        return coinMagnetTicks > 0;
    }

    public boolean isInvincibilityActive() {
        return invincibilityTicks > 0;
    }

    public int getSpeedBoostTicks() {
        return speedBoostTicks;
    }

    public int getCoinMagnetTicks() {
        return coinMagnetTicks;
    }

    public int getInvincibilityTicks() {
        return invincibilityTicks;
    }

    public void startGame(boolean isMultiplayer, String p2Name) {
        this.isMultiplayer = isMultiplayer;
        this.player2Name = p2Name != null && !p2Name.trim().isEmpty() ? p2Name : "Player 2";
        this.gameBoard = new GameBoard(Constants.GRID_WIDTH, Constants.GRID_HEIGHT);

        this.snake1 = new Snake(Constants.GRID_WIDTH / 4, Constants.GRID_HEIGHT / 2, Direction.RIGHT,
                Constants.COLOR_SNAKE_1_HEAD, Constants.COLOR_SNAKE_1_BODY);

        if (activePlayer != null) {
            this.snake1.setSkinName(activePlayer.getSelectedSkin());
        }

        if (isMultiplayer) {
            this.snake2 = new Snake(3 * Constants.GRID_WIDTH / 4, Constants.GRID_HEIGHT / 2, Direction.LEFT,
                    Constants.COLOR_SNAKE_2_HEAD, Constants.COLOR_SNAKE_2_BODY);
        } else {
            this.snake2 = null;
        }

        this.keyboardHandler.setSnakes(snake1, snake2, isMultiplayer);

        this.score1 = 3; // Initial size
        this.score2 = isMultiplayer ? 3 : 0;
        this.coinsCollected = 0;
        this.level = 1;
        this.levelUpFlashTicks = 0;
        this.isPaused = false;
        this.isGameOver = false;
        this.startTime = System.currentTimeMillis();
        this.speedBoostTicks = 0;
        this.coinMagnetTicks = 0;
        this.invincibilityTicks = 0;
        this.baseDelay = Constants.GAME_SPEED_NORMAL;
        this.timer.setDelay(Constants.GAME_SPEED_NORMAL);

        // Generate initial resources
        gameBoard.generateResource(snake1, snake2);
        gameBoard.generateResource(snake1, snake2);

        timer.start();

        // Apply any pending effects bought from the store
        for (String effect : pendingEffects) {
            applyEffectNow(effect);
        }
        pendingEffects.clear();

        if (gamePanel != null)
            gamePanel.repaint();
    }

    public void pauseOrResume() {
        if (isGameOver)
            return;

        isPaused = !isPaused;
        if (isPaused) {
            timer.stop();
        } else {
            timer.start();
        }
        if (gamePanel != null)
            gamePanel.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isPaused && !isGameOver) {
            updateGame();
            if (gamePanel != null)
                gamePanel.repaint();
        }
    }

    private void updateGame() {
        if (levelUpFlashTicks > 0) {
            levelUpFlashTicks--;
        }

        // Decrement effect timers
        if (speedBoostTicks > 0) {
            speedBoostTicks--;
            if (speedBoostTicks == 0) {
                applySpeedBoost(false);
            }
        }
        if (coinMagnetTicks > 0)
            coinMagnetTicks--;
        if (invincibilityTicks > 0)
            invincibilityTicks--;

        // Move snakes
        if (snake1.isAlive())
            snake1.move();
        if (isMultiplayer && snake2.isAlive())
            snake2.move();

        // Check collisions
        checkCollisions();

        // Check resources consumption
        checkResources();

        // Check if game is over
        if (isMultiplayer) {
            if (!snake1.isAlive() && !snake2.isAlive()) {
                endGame();
            }
        } else {
            if (!snake1.isAlive()) {
                endGame();
            }
        }
    }

    private void checkCollisions() {
        // Wall and self collision (skip if invincible)
        if (snake1.isAlive() && snake1.checkCollision(Constants.GRID_WIDTH, Constants.GRID_HEIGHT)) {
            if (invincibilityTicks > 0) {
                // Wrap around instead of dying
                Segment head = snake1.getHead();
                if (head.getX() < 0)
                    head.setX(Constants.GRID_WIDTH - 1);
                if (head.getX() >= Constants.GRID_WIDTH)
                    head.setX(0);
                if (head.getY() < 0)
                    head.setY(Constants.GRID_HEIGHT - 1);
                if (head.getY() >= Constants.GRID_HEIGHT)
                    head.setY(0);
            } else {
                snake1.setAlive(false);
            }
        }

        if (isMultiplayer && snake2.isAlive() && snake2.checkCollision(Constants.GRID_WIDTH, Constants.GRID_HEIGHT)) {
            snake2.setAlive(false);
        }

        // Collision between snakes
        if (isMultiplayer && snake1.isAlive() && snake2.isAlive()) {
            if (snake1.checkCollisionWith(snake2)) {
                if (invincibilityTicks <= 0)
                    snake1.setAlive(false);
            }
            if (snake2.checkCollisionWith(snake1)) {
                snake2.setAlive(false);
            }
        }

        // Self collision (skip for player 1 if invincible)
        if (snake1.isAlive() && invincibilityTicks <= 0) {
            Segment head = snake1.getHead();
            for (int i = 1; i < snake1.getBody().size(); i++) {
                if (head.equals(snake1.getBody().get(i))) {
                    snake1.setAlive(false);
                    break;
                }
            }
        }
    }

    private void checkResources() {
        Resource consumedResource = null;
        Snake consumer = null;

        for (Resource r : gameBoard.getResources()) {
            if (snake1.isAlive() && snake1.getHead().getX() == r.getX() && snake1.getHead().getY() == r.getY()) {
                consumedResource = r;
                consumer = snake1;
                break;
            }
            if (isMultiplayer && snake2.isAlive() && snake2.getHead().getX() == r.getX()
                    && snake2.getHead().getY() == r.getY()) {
                consumedResource = r;
                consumer = snake2;
                break;
            }
        }

        if (consumedResource != null) {
            gameBoard.removeResource(consumedResource);

            if (consumedResource instanceof PositiveFood) {
                consumer.grow();
                if (consumer == snake1)
                    score1++;
                else
                    score2++;
                checkLevelUp();
            } else if (consumedResource instanceof NegativeFood) {
                consumer.shrink();
                if (consumer == snake1)
                    score1--;
                else
                    score2--;
            } else if (consumedResource instanceof Coin) {
                int coinValue = consumedResource.getValue();
                if (coinMagnetTicks > 0)
                    coinValue *= 10; // Coin Magnet doubles value
                coinsCollected += coinValue;
            }

            // Generate a new resource
            gameBoard.generateResource(snake1, snake2);
        }

        // Random chance to spawn extra coins or resources
        if (Math.random() < 0.05 && gameBoard.getResources().size() < 5) {
            gameBoard.generateResource(snake1, snake2);
        }
    }

    private void checkLevelUp() {
        int maxScore = Math.max(score1, score2);
        int newLevel = 1 + (maxScore - 3) / 5;
        if (newLevel > level) {
            level = newLevel;
            levelUpFlashTicks = 15; // Set flash animation ticks
            baseDelay = Math.max(30, Constants.GAME_SPEED_NORMAL - (level - 1) * 12);
            if (speedBoostTicks > 0) {
                applySpeedBoost(true);
            } else {
                timer.setDelay(baseDelay);
            }
        }
    }

    private boolean lastSaveWasOnline = false;

    public boolean wasLastSaveOnline() {
        return lastSaveWasOnline;
    }

    private void endGame() {
        isGameOver = true;
        timer.stop();

        long endTime = System.currentTimeMillis();
        int durationSeconds = (int) ((endTime - startTime) / 1000);

        database.ScoreDAO scoreDAO = new database.ScoreDAOImpl();
        String player1Name = activePlayer != null ? activePlayer.getUsername() : "Player 1";

        if (database.DatabaseManager.isConnected()) {
            // Sauvegarde en base de données
            scoreDAO.saveScore(player1Name, score1, durationSeconds, level, coinsCollected);
            if (isMultiplayer) {
                scoreDAO.saveScore(player2Name, score2, durationSeconds, 1, 0);
            }
            if (activePlayer != null) {
                int newCoins = activePlayer.getTotalCoins() + coinsCollected;
                activePlayer.setTotalCoins(newCoins);
                scoreDAO.updatePlayerCoins(player1Name, newCoins);
            }
            lastSaveWasOnline = true;
        } else {
            // Mode hors ligne : les coins en mémoire sont conservés mais non persistés
            if (activePlayer != null) {
                activePlayer.setTotalCoins(activePlayer.getTotalCoins() + coinsCollected);
            }
            lastSaveWasOnline = false;
            System.err.println("⚠ Partie terminée hors ligne : score non enregistré en base.");
        }
    }

    // Getters for View
    public Snake getSnake1() {
        return snake1;
    }

    public Snake getSnake2() {
        return snake2;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public int getScore1() {
        return score1;
    }

    public int getScore2() {
        return score2;
    }

    public int getCoinsCollected() {
        return coinsCollected;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public boolean isMultiplayer() {
        return isMultiplayer;
    }

    public int getLevel() {
        return level;
    }
}
