package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.LinkedList;
import utils.Constants;

public class Snake {
    private LinkedList<Segment> body;
    private Direction currentDirection;
    private Direction nextDirection;
    private Color headColor;
    private Color bodyColor;
    private boolean isAlive;
    private String skinName = "default";

    public Snake(int startX, int startY, Direction startDir, Color headColor, Color bodyColor) {
        this.body = new LinkedList<>();
        // Start with 3 segments
        for (int i = 0; i < 3; i++) {
            if (startDir == Direction.RIGHT) body.add(new Segment(startX - i, startY));
            else if (startDir == Direction.LEFT) body.add(new Segment(startX + i, startY));
            else if (startDir == Direction.UP) body.add(new Segment(startX, startY + i));
            else if (startDir == Direction.DOWN) body.add(new Segment(startX, startY - i));
        }
        this.currentDirection = startDir;
        this.nextDirection = startDir;
        this.headColor = headColor;
        this.bodyColor = bodyColor;
        this.isAlive = true;
    }

    public void setDirection(Direction dir) {
        if (!currentDirection.isOpposite(dir)) {
            nextDirection = dir;
        }
    }

    public void move() {
        if (!isAlive) return;

        currentDirection = nextDirection;
        Segment head = body.getFirst();
        int newX = head.getX();
        int newY = head.getY();

        switch (currentDirection) {
            case UP: newY--; break;
            case DOWN: newY++; break;
            case LEFT: newX--; break;
            case RIGHT: newX++; break;
        }

        // Add new head
        body.addFirst(new Segment(newX, newY));
        // Remove tail
        body.removeLast();
    }

    public void grow() {
        // Add a dummy segment that will be placed correctly on the next move
        Segment tail = body.getLast();
        body.addLast(new Segment(tail.getX(), tail.getY()));
    }

    public void shrink() {
        if (body.size() > 3) {
            body.removeLast();
        } else {
            isAlive = false;
        }
    }

    public boolean checkCollision(int width, int height) {
        Segment head = body.getFirst();

        // Wall collision
        if (head.getX() < 0 || head.getX() >= width || head.getY() < 0 || head.getY() >= height) {
            return true;
        }

        // Self collision
        for (int i = 1; i < body.size(); i++) {
            if (head.equals(body.get(i))) {
                return true;
            }
        }

        return false;
    }

    public boolean checkCollisionWith(Snake other) {
        Segment head = body.getFirst();
        for (Segment segment : other.getBody()) {
            if (head.equals(segment)) {
                return true;
            }
        }
        return false;
    }

    public void draw(Graphics2D g, int tileSize) {
        java.awt.Composite originalComposite = g.getComposite();
        
        for (int i = 0; i < body.size(); i++) {
            Segment segment = body.get(i);
            int x = segment.getX() * tileSize;
            int y = segment.getY() * tileSize;
            
            // Set drawing style based on selected skin
            String skin = skinName.toLowerCase().trim();
            Color currentHeadColor = headColor;
            Color currentBodyColor = bodyColor;
            
            if (skin.contains("neon")) {
                currentHeadColor = new java.awt.Color(0, 255, 255); // Cyan
                currentBodyColor = new java.awt.Color(255, 0, 128); // Hot Pink
            } else if (skin.contains("gold")) {
                currentHeadColor = new java.awt.Color(255, 215, 0); // Gold
                currentBodyColor = new java.awt.Color(184, 134, 11); // Dark Gold
            } else if (skin.contains("diamond")) {
                currentHeadColor = new java.awt.Color(224, 255, 255); // Ice Blue
                currentBodyColor = new java.awt.Color(95, 158, 160); // Cadet Blue
            } else if (skin.contains("fire")) {
                // Flowing Fire colors
                int red = 255;
                int green = (int) (80 + 100 * Math.sin(i * 0.4 + System.currentTimeMillis() * 0.015));
                currentBodyColor = new java.awt.Color(red, Math.max(0, Math.min(255, green)), 0);
                currentHeadColor = new java.awt.Color(255, 69, 0); // Orange Red
            } else if (skin.contains("ice")) {
                currentHeadColor = java.awt.Color.WHITE;
                currentBodyColor = new java.awt.Color(173, 216, 230); // Light Blue
            } else if (skin.contains("phantom")) {
                currentHeadColor = new java.awt.Color(138, 43, 226); // Blue Violet
                currentBodyColor = new java.awt.Color(72, 61, 139); // Dark Slate Blue
                g.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.45f));
            } else if (skin.contains("rainbow")) {
                float hue = (float) ((i * 25 - System.currentTimeMillis() / 8) % 360) / 360.0f;
                if (hue < 0) hue += 1.0f;
                Color rain = java.awt.Color.getHSBColor(hue, 0.9f, 0.95f);
                currentHeadColor = java.awt.Color.getHSBColor((hue + 0.1f) % 1.0f, 0.9f, 0.95f);
                currentBodyColor = rain;
            }

            if (i == 0) {
                g.setColor(currentHeadColor);
                if (skin.contains("diamond")) {
                    // Draw diamond head
                    java.awt.Polygon diamond = new java.awt.Polygon();
                    diamond.addPoint(x + tileSize / 2, y);
                    diamond.addPoint(x + tileSize, y + tileSize / 2);
                    diamond.addPoint(x + tileSize / 2, y + tileSize);
                    diamond.addPoint(x, y + tileSize / 2);
                    g.fillPolygon(diamond);
                } else if (skin.contains("gold")) {
                    // Draw gold head with crown
                    g.fillRoundRect(x, y, tileSize, tileSize, 12, 12);
                    g.setColor(new java.awt.Color(255, 255, 0));
                    // Simple small crown on top
                    java.awt.Polygon crown = new java.awt.Polygon();
                    crown.addPoint(x + 2, y + 4);
                    crown.addPoint(x + 5, y);
                    crown.addPoint(x + 8, y + 3);
                    crown.addPoint(x + 11, y);
                    crown.addPoint(x + 14, y + 4);
                    g.fillPolygon(crown);
                } else {
                    g.fillRoundRect(x, y, tileSize, tileSize, 10, 10);
                }
            } else {
                g.setColor(currentBodyColor);
                int padding = 2;
                if (skin.contains("diamond")) {
                    // Draw diamond body segment
                    java.awt.Polygon diamond = new java.awt.Polygon();
                    diamond.addPoint(x + tileSize / 2, y + padding);
                    diamond.addPoint(x + tileSize - padding, y + tileSize / 2);
                    diamond.addPoint(x + tileSize / 2, y + tileSize - padding);
                    diamond.addPoint(x + padding, y + tileSize / 2);
                    g.fillPolygon(diamond);
                } else if (skin.contains("ice")) {
                    // Ice block look with inner border
                    g.fillRect(x + padding, y + padding, tileSize - 2 * padding, tileSize - 2 * padding);
                    g.setColor(java.awt.Color.WHITE);
                    g.drawRect(x + padding + 1, y + padding + 1, tileSize - 2 * padding - 2, tileSize - 2 * padding - 2);
                } else if (skin.contains("neon")) {
                    // Glowing inner border
                    g.fillRoundRect(x + padding, y + padding, tileSize - 2 * padding, tileSize - 2 * padding, 8, 8);
                    g.setColor(java.awt.Color.WHITE);
                    g.drawRoundRect(x + padding, y + padding, tileSize - 2 * padding, tileSize - 2 * padding, 8, 8);
                } else {
                    g.fillRoundRect(x + padding, y + padding, tileSize - 2 * padding, tileSize - 2 * padding, 8, 8);
                }
            }
        }
        
        // Restore opacity if phantom skin was used
        g.setComposite(originalComposite);
    }

    public Segment getHead() {
        return body.getFirst();
    }

    public LinkedList<Segment> getBody() {
        return body;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        this.isAlive = alive;
    }

    public void setSkinName(String skinName) {
        this.skinName = skinName != null ? skinName.toLowerCase() : "default";
    }

    public String getSkinName() {
        return skinName;
    }
}
