package view;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import controller.GameController;
import model.Resource;
import utils.Constants;

public class GamePanel extends JPanel {
    private MainFrame mainFrame;
    private GameController gameController;

    public GamePanel(MainFrame mainFrame, GameController gameController) {
        this.mainFrame = mainFrame;
        this.gameController = gameController;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawBackground(g2d);
        drawGrid(g2d);
        drawGame(g2d);
        
        if (gameController.getLevelUpFlashTicks() > 0) {
            drawLevelUp(g2d);
        }

        drawUI(g2d);
    }
    
    private void drawBackground(Graphics2D g) {
        GradientPaint gp = new GradientPaint(0, 0, Constants.COLOR_BACKGROUND, 0, getHeight(), new Color(10, 10, 15));
        g.setPaint(gp);
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    private void drawGrid(Graphics2D g) {
        g.setColor(Constants.COLOR_GRID);
        g.setStroke(new BasicStroke(1));
        for (int i = 0; i <= Constants.GRID_WIDTH; i++) {
            g.drawLine(i * Constants.TILE_SIZE, 0, i * Constants.TILE_SIZE, Constants.GRID_HEIGHT * Constants.TILE_SIZE);
        }
        for (int i = 0; i <= Constants.GRID_HEIGHT; i++) {
            g.drawLine(0, i * Constants.TILE_SIZE, Constants.GRID_WIDTH * Constants.TILE_SIZE, i * Constants.TILE_SIZE);
        }
    }

    private void drawGame(Graphics2D g) {
        if (gameController.getGameBoard() == null) return;

        // Draw resources
        for (Resource r : gameController.getGameBoard().getResources()) {
            r.draw(g, Constants.TILE_SIZE);
        }

        // Draw snakes
        if (gameController.getSnake1() != null && gameController.getSnake1().isAlive()) {
            gameController.getSnake1().draw(g, Constants.TILE_SIZE);
        }
        if (gameController.isMultiplayer() && gameController.getSnake2() != null && gameController.getSnake2().isAlive()) {
            gameController.getSnake2().draw(g, Constants.TILE_SIZE);
        }
    }

    private void drawUI(Graphics2D g) {
        // Draw UI panel on the right
        int uiX = Constants.GRID_WIDTH * Constants.TILE_SIZE;
        int uiWidth = getWidth() - uiX;
        
        g.setColor(Constants.COLOR_UI_PANEL);
        g.fillRect(uiX, 0, uiWidth, getHeight());
        
        g.setColor(Constants.COLOR_GRID);
        g.drawLine(uiX, 0, uiX, getHeight());

        uiX += 20; // padding
        
        g.setColor(Constants.COLOR_TEXT_MUTED);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("PLAYER 1", uiX, 40);
        g.setColor(Constants.COLOR_SNAKE_1_HEAD);
        g.setFont(Constants.FONT_TITLE.deriveFont(32f));
        g.drawString(String.valueOf(gameController.getScore1()), uiX, 75);

        if (gameController.isMultiplayer()) {
            g.setColor(Constants.COLOR_TEXT_MUTED);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString("PLAYER 2", uiX, 130);
            g.setColor(Constants.COLOR_SNAKE_2_HEAD);
            g.setFont(Constants.FONT_TITLE.deriveFont(32f));
            g.drawString(String.valueOf(gameController.getScore2()), uiX, 165);
        }

        g.setColor(Constants.COLOR_TEXT_MUTED);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("COINS", uiX, 230);
        g.setColor(Constants.COLOR_COIN);
        g.setFont(Constants.FONT_TITLE.deriveFont(32f));
        g.drawString(String.valueOf(gameController.getCoinsCollected()), uiX, 265);

        g.setColor(Constants.COLOR_TEXT_MUTED);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("LEVEL", uiX, 330);
        g.setColor(Color.WHITE);
        g.setFont(Constants.FONT_TITLE.deriveFont(32f));
        g.drawString(String.valueOf(gameController.getLevel()), uiX, 365);

        g.setColor(Constants.COLOR_TEXT_MUTED);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("Press ESC to Pause", uiX, Constants.WINDOW_HEIGHT - 60);

        if (gameController.isPaused()) {
            drawOverlay(g, "PAUSED", "Press ESC to resume");
        } else if (gameController.isGameOver()) {
            drawOverlay(g, "GAME OVER", "Press ESC for menu");
        }
    }
    
    private void drawOverlay(Graphics2D g, String title, String subtitle) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Constants.GRID_WIDTH * Constants.TILE_SIZE, Constants.GRID_HEIGHT * Constants.TILE_SIZE);

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        g.setColor(Color.WHITE);
        g.setFont(Constants.FONT_TITLE);
        int strWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (Constants.GRID_WIDTH * Constants.TILE_SIZE - strWidth) / 2, Constants.GRID_HEIGHT * Constants.TILE_SIZE / 2);

        g.setFont(Constants.FONT_MENU);
        g.setColor(Constants.COLOR_TEXT_MUTED);
        int subWidth = g.getFontMetrics().stringWidth(subtitle);
        g.drawString(subtitle, (Constants.GRID_WIDTH * Constants.TILE_SIZE - subWidth) / 2, Constants.GRID_HEIGHT * Constants.TILE_SIZE / 2 + 40);

        // CORRECTION : avertissement si la partie n'a pas été sauvegardée
        if (gameController.isGameOver() && !gameController.wasLastSaveOnline()) {
            String warn = "⚠ Score non sauvegardé (base de données hors ligne)";
            g.setFont(new Font("Arial", Font.BOLD, 13));
            g.setColor(Constants.COLOR_FOOD_POS);
            int warnWidth = g.getFontMetrics().stringWidth(warn);
            g.drawString(warn, (Constants.GRID_WIDTH * Constants.TILE_SIZE - warnWidth) / 2,
                         Constants.GRID_HEIGHT * Constants.TILE_SIZE / 2 + 75);
        }
    }

    private void drawLevelUp(Graphics2D g) {
        g.setFont(Constants.FONT_TITLE.deriveFont(54f));
        String text = "LEVEL UP!";
        
        // Shadow effect
        g.setColor(Color.BLACK);
        int shadowOffset = 3;
        int strWidth = g.getFontMetrics().stringWidth(text);
        int x = (Constants.GRID_WIDTH * Constants.TILE_SIZE - strWidth) / 2;
        int y = Constants.GRID_HEIGHT * Constants.TILE_SIZE / 2;
        g.drawString(text, x + shadowOffset, y + shadowOffset);
        
        // Neon color pulsing
        g.setColor(Constants.COLOR_COIN);
        g.drawString(text, x, y);
        
        g.setFont(Constants.FONT_MENU);
        g.setColor(Color.WHITE);
        String subText = "Speed Increased!";
        int subWidth = g.getFontMetrics().stringWidth(subText);
        g.drawString(subText, (Constants.GRID_WIDTH * Constants.TILE_SIZE - subWidth) / 2, y + 45);
    }
}
