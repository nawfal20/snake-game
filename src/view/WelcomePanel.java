package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import utils.Constants;

public class WelcomePanel extends JPanel {
    private MainFrame mainFrame;
    private float animationAlpha = 0f;
    private Timer fadeTimer;
    private int snakeAnimOffset = 0;
    private Timer snakeAnimTimer;

    public WelcomePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        // Main scrollable content
        ScrollablePanel content = new ScrollablePanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(30, 60, 40, 60));

        // === HERO SECTION ===
        JPanel heroPanel = createHeroSection();
        content.add(heroPanel);
        content.add(Box.createVerticalStrut(30));

        // === ABOUT SECTION ===
        JPanel aboutCard = createSectionCard(
                "🎮 About the Game",
                "Snake Game Pro is a modern take on the classic Snake game.\n" +
                        "Eat food to grow longer, collect coins to buy skins and effects,\n" +
                        "and compete for the highest score on the leaderboard!\n\n" +
                        "Features:\n" +
                        "  • Single player and 2-player local multiplayer\n" +
                        "  • 7 unique snake skins (Neon, Gold, Diamond, Fire, Ice, Phantom, Rainbow)\n" +
                        "  • 3 power-up effects (Speed Boost, Coin Magnet, Invincibility)\n" +
                        "  • Progressive difficulty with level system\n" +
                        "  • Online scoreboard with MySQL database\n" +
                        "  • Store to buy and equip cosmetics",
                new Color(0, 255, 128));
        content.add(aboutCard);
        content.add(Box.createVerticalStrut(20));

        // === HOW TO PLAY ===
        JPanel howToPlayCard = createSectionCard(
                "📖 How to Play",
                "Guide your snake around the grid to eat food and collect coins.\n" +
                        "Each food item makes your snake grow longer and increases your score.\n\n" +
                        "Avoid:\n" +
                        "  ✖ Hitting the walls — instant death!\n" +
                        "  ✖ Running into your own body\n" +
                        "  ✖ Purple poison food — shrinks your snake\n\n" +
                        "Collect:\n" +
                        "  ✔ Red food — grow and score +1\n" +
                        "  ✔ Gold coins — earn currency for the store\n\n" +
                        "Every 5 points, you level up and the game gets faster!",
                new Color(255, 215, 0));
        content.add(howToPlayCard);
        content.add(Box.createVerticalStrut(20));

        // === CONTROLS ===
        JPanel controlsCard = createControlsSection();
        content.add(controlsCard);
        content.add(Box.createVerticalStrut(20));

        // === STORE & EFFECTS ===
        JPanel storeCard = createSectionCard(
                "🛒 Store & Effects",
                "Earn coins during gameplay and spend them in the Store!\n\n" +
                        "Skins — Change your snake's appearance:\n" +
                        "  🐍 Neon Snake (100c) — Cyan & Pink glow\n" +
                        "  👑 Golden Snake (500c) — Gold with crown\n" +
                        "  💎 Diamond Snake (1000c) — Diamond-shaped segments\n" +
                        "  🔥 Fire Snake (300c) — Flowing fire animation\n" +
                        "  ❄ Ice Snake (300c) — Frozen ice blocks\n" +
                        "  👻 Phantom Snake (800c) — Semi-transparent ghost\n" +
                        "  🌈 Rainbow Snake (1500c) — Animated rainbow colors\n\n" +
                        "Effects — Temporary power-ups:\n" +
                        "  ⚡ Speed Boost (50c) — Move 35% faster for ~9 seconds\n" +
                        "  🧲 Coin Magnet (200c) — Coins worth 10x value for ~30 seconds\n" +
                        "  🛡 Invincibility (1000c) — Pass through walls for ~30 seconds",
                new Color(200, 150, 255));
        content.add(storeCard);
        content.add(Box.createVerticalStrut(20));

        // === TIPS ===
        JPanel tipsCard = createSectionCard(
                "💡 Tips & Tricks",
                "• Buy effects BEFORE starting a game — they activate at game start\n" +
                        "• Coin Magnet is the best value — earn coins 10x faster!\n" +
                        "• Use Invincibility to safely cross the entire map\n" +
                        "• In 2-player mode, try to trap your opponent\n" +
                        "• Higher levels = faster speed = more challenge\n" +
                        "• The Admin panel lets you customize item prices",
                new Color(0, 200, 255));
        content.add(tipsCard);
        content.add(Box.createVerticalStrut(40));

        // === ENTER BUTTON ===
        JPanel enterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        enterPanel.setOpaque(false);
        enterPanel.setMaximumSize(new Dimension(800, 80));
        enterPanel.setAlignmentX(CENTER_ALIGNMENT);

        JButton btnEnter = createGlowButton("🎮  PLAY NOW  →", Constants.COLOR_SNAKE_1_HEAD);
        btnEnter.addActionListener(e -> mainFrame.showPanel("MENU"));
        enterPanel.add(btnEnter);

        content.add(enterPanel);
        content.add(Box.createVerticalStrut(30));

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // Style the scrollbar
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));

        add(scrollPane, BorderLayout.CENTER);

        // Footer
        JLabel footer = new JLabel("Snake Game Pro — v2.0 — Press ENTER or click Play Now", JLabel.CENTER);
        footer.setFont(new Font("Arial", Font.ITALIC, 12));
        footer.setForeground(Constants.COLOR_TEXT_MUTED);
        footer.setBorder(new EmptyBorder(10, 0, 15, 0));
        add(footer, BorderLayout.SOUTH);

        // Fade-in animation
        fadeTimer = new Timer(30, e -> {
            animationAlpha = Math.min(1f, animationAlpha + 0.04f);
            repaint();
            if (animationAlpha >= 1f)
                fadeTimer.stop();
        });
        fadeTimer.start();

        // Snake decoration animation
        snakeAnimTimer = new Timer(100, e -> {
            snakeAnimOffset = (snakeAnimOffset + 1) % 200;
            repaint();
        });
        snakeAnimTimer.start();

        // Key binding: ENTER to proceed
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0), "enter");
        getActionMap().put("enter", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                mainFrame.showPanel("MENU");
            }
        });
    }

    private JPanel createHeroSection() {
        JPanel hero = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background gradient card
                GradientPaint gp = new GradientPaint(0, 0, new Color(15, 40, 30), getWidth(), getHeight(),
                        new Color(10, 15, 35));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 24, 24));

                // Animated snake decoration at top
                drawDecorativeSnake(g2, getWidth());

                // Border glow
                g2.setColor(new Color(0, 255, 128, 60));
                g2.setStroke(new BasicStroke(2));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 3, getHeight() - 3, 24, 24));
                g2.dispose();
            }
        };
        hero.setOpaque(false);
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));
        hero.setBorder(new EmptyBorder(40, 30, 35, 30));
        hero.setMaximumSize(new Dimension(800, 220));
        hero.setAlignmentX(CENTER_ALIGNMENT);

        JLabel title = new JLabel("SNAKE GAME PRO");
        title.setFont(new Font("Arial", Font.BOLD, 52));
        title.setForeground(Constants.COLOR_SNAKE_1_HEAD);
        title.setAlignmentX(CENTER_ALIGNMENT);
        hero.add(title);

        hero.add(Box.createVerticalStrut(10));

        JLabel subtitle = new JLabel("The Ultimate Snake Experience");
        subtitle.setFont(new Font("Arial", Font.ITALIC, 20));
        subtitle.setForeground(new Color(150, 255, 200));
        subtitle.setAlignmentX(CENTER_ALIGNMENT);
        hero.add(subtitle);

        hero.add(Box.createVerticalStrut(8));

        JLabel version = new JLabel("Version 2.0 — With Skins, Effects & Multiplayer");
        version.setFont(new Font("Arial", Font.PLAIN, 14));
        version.setForeground(Constants.COLOR_TEXT_MUTED);
        version.setAlignmentX(CENTER_ALIGNMENT);
        hero.add(version);

        return hero;
    }

    private void drawDecorativeSnake(Graphics2D g, int panelWidth) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int segmentSize = 12;
        int y = 15;

        for (int i = 0; i < 20; i++) {
            int x = ((snakeAnimOffset + i * 18) % (panelWidth + 100)) - 50;
            float hue = (float) ((i * 18 + snakeAnimOffset * 5) % 360) / 360f;
            Color c = Color.getHSBColor(hue, 0.7f, 0.9f);
            g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 100));
            g.fillRoundRect(x, y + (int) (Math.sin(i * 0.5 + snakeAnimOffset * 0.15) * 6), segmentSize, segmentSize, 4,
                    4);
        }
    }

    private JPanel createSectionCard(String title, String body, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(20, 20, 32, 220));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));

                // Accent bar on left
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 8, 4, getHeight() - 16, 4, 4);

                // Border
                g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 40));
                g2.setStroke(new BasicStroke(1));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 18, 18));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 24, 20, 20));
        card.setMaximumSize(new Dimension(800, 400));
        card.setAlignmentX(CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(accentColor);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        card.add(titleLabel);

        card.add(Box.createVerticalStrut(12));

        // Split body into lines for better rendering
        for (String line : body.split("\n")) {
            JLabel lineLabel = new JLabel(line);
            lineLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            lineLabel.setForeground(new Color(210, 210, 220));
            lineLabel.setAlignmentX(LEFT_ALIGNMENT);
            card.add(lineLabel);
            card.add(Box.createVerticalStrut(3));
        }

        return card;
    }

    private JPanel createControlsSection() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(20, 20, 32, 220));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));

                Color accent = new Color(255, 100, 100);
                g2.setColor(accent);
                g2.fillRoundRect(0, 8, 4, getHeight() - 16, 4, 4);

                g2.setColor(new Color(255, 100, 100, 40));
                g2.setStroke(new BasicStroke(1));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 18, 18));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 24, 20, 20));
        card.setMaximumSize(new Dimension(800, 350));
        card.setAlignmentX(CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("🎯 Controls");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(255, 100, 100));
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        card.add(titleLabel);

        card.add(Box.createVerticalStrut(15));

        // Controls grid
        JPanel gridPanel = new JPanel(new GridLayout(0, 2, 30, 8));
        gridPanel.setOpaque(false);
        gridPanel.setMaximumSize(new Dimension(700, 200));
        gridPanel.setAlignmentX(LEFT_ALIGNMENT);

        // Player 1
        addControlHeader(gridPanel, "Player 1", Constants.COLOR_SNAKE_1_HEAD);
        addControlHeader(gridPanel, "Player 2", Constants.COLOR_SNAKE_2_HEAD);

        addControlRow(gridPanel, "↑  ↓  ←  →", "Arrow keys to move");
        addControlRow(gridPanel, "Z  S  Q  D", "ZQSD keys to move");

        addControlRow(gridPanel, "ESC", "Pause / Resume");
        addControlRow(gridPanel, "ESC", "Back to menu (game over)");

        card.add(gridPanel);

        return card;
    }

    private void addControlHeader(JPanel panel, String title, Color color) {
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        lbl.setForeground(color);
        panel.add(lbl);
    }

    private void addControlRow(JPanel panel, String keys, String action) {
        JLabel keyLabel = new JLabel("  [ " + keys + " ]");
        keyLabel.setFont(new Font("Consolas", Font.BOLD, 13));
        keyLabel.setForeground(new Color(255, 200, 100));
        panel.add(keyLabel);

        JLabel actionLabel = new JLabel("  " + action);
        actionLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        actionLabel.setForeground(Constants.COLOR_TEXT_MUTED);
        panel.add(actionLabel);
    }

    private JButton createGlowButton(String text, Color accentColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Glow effect
                if (getModel().isRollover()) {
                    g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 30));
                    g2.fillRoundRect(-4, -4, getWidth() + 8, getHeight() + 8, 24, 24);
                    g2.setColor(accentColor);
                } else {
                    g2.setColor(Constants.COLOR_UI_PANEL);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Border
                g2.setColor(accentColor);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Arial", Font.BOLD, 26));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(350, 65));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(accentColor);
            }

            public void mouseExited(MouseEvent e) {
                btn.setForeground(Color.WHITE);
            }
        });
        return btn;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        // Background gradient
        GradientPaint gp = new GradientPaint(0, 0, new Color(8, 8, 15), 0, getHeight(), new Color(15, 10, 25));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Fade-in effect
        if (animationAlpha < 1f) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f - animationAlpha));
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        g2d.dispose();
    }

    public void stopAnimations() {
        if (fadeTimer != null)
            fadeTimer.stop();
        if (snakeAnimTimer != null)
            snakeAnimTimer.stop();
    }

    private static class ScrollablePanel extends JPanel implements Scrollable {
        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 50;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}
