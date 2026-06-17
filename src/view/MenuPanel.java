package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.Player;
import database.ScoreDAO;
import database.ScoreDAOImpl;
import utils.Constants;

public class MenuPanel extends JPanel {
    private MainFrame mainFrame;
    private JLabel lblPlayerName;
    private JLabel lblPlayerCoins;
    private JLabel lblPlayerBest;
    private JLabel lblDbStatus;
    
    private JButton[] menuButtons;
    private int selectedButtonIndex = 0;

    public MenuPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setFocusable(true);

        // Profile header panel
        JPanel profileHeader = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(25, 25, 35, 180));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(Constants.COLOR_SNAKE_1_HEAD);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();
            }
        };
        profileHeader.setOpaque(false);
        profileHeader.setLayout(new FlowLayout(FlowLayout.CENTER, 25, 10));
        profileHeader.setMaximumSize(new Dimension(800, 50));
        profileHeader.setBorder(new EmptyBorder(5, 15, 5, 15));

        lblPlayerName = new JLabel("Player: ---");
        lblPlayerName.setFont(new Font("Arial", Font.BOLD, 14));
        lblPlayerName.setForeground(Color.WHITE);

        lblPlayerCoins = new JLabel("💰 0");
        lblPlayerCoins.setFont(new Font("Arial", Font.BOLD, 14));
        lblPlayerCoins.setForeground(Constants.COLOR_COIN);

        lblPlayerBest = new JLabel("🏆 Best: 0");
        lblPlayerBest.setFont(new Font("Arial", Font.BOLD, 14));
        lblPlayerBest.setForeground(Constants.COLOR_FOOD_POS);

        JButton btnChangeProfile = createSmallButton("Change Profile");
        btnChangeProfile.addActionListener(e -> changeProfileDialog());

        profileHeader.add(lblPlayerName);
        profileHeader.add(lblPlayerCoins);
        profileHeader.add(lblPlayerBest);
        profileHeader.add(btnChangeProfile);

        JLabel titleLabel = new JLabel("SNAKE GAME PRO");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 56));
        titleLabel.setForeground(Constants.COLOR_SNAKE_1_HEAD);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton btn1Player = createModernButton("1 Player", Constants.COLOR_SNAKE_1_HEAD);
        btn1Player.addActionListener(e -> mainFrame.startGame(false, null));

        JButton btn2Players = createModernButton("2 Players Local", Constants.COLOR_SNAKE_2_HEAD);
        btn2Players.addActionListener(e -> {
            String currentP1Name = mainFrame.getActivePlayer() != null ? mainFrame.getActivePlayer().getUsername() : "Player 1";
            String p1Name = promptForPlayerName("Player 1", currentP1Name);
            
            if (p1Name != null) {
                Player p1 = ensurePlayerExists(p1Name);
                mainFrame.setActivePlayer(p1);
                
                String p2Name = promptForPlayerName("Player 2", "Player 2");
                if (p2Name != null) {
                    ensurePlayerExists(p2Name);
                    mainFrame.startGame(true, p2Name);
                }
            }
        });

        JButton btnStore = createModernButton("Store / Shop", Constants.COLOR_COIN);
        btnStore.addActionListener(e -> mainFrame.showPanel("STORE"));

        JButton btnScores = createModernButton("Scores", Constants.COLOR_FOOD_POS);
        btnScores.addActionListener(e -> mainFrame.showPanel("SCORES"));
        
        JButton btnSettings = createModernButton("Settings & Help", new Color(200, 150, 255));
        btnSettings.addActionListener(e -> mainFrame.showPanel("SETTINGS"));

        JButton btnAdmin = createModernButton("⚙ Admin / Manage Prices", new Color(180, 100, 255));
        btnAdmin.addActionListener(e -> mainFrame.showPanel("ADMIN"));

        JButton btnQuit = createModernButton("Quit", Color.GRAY);
        btnQuit.addActionListener(e -> {
            database.DatabaseManager.closeConnection();
            System.exit(0);
        });
        
        menuButtons = new JButton[] { btn1Player, btn2Players, btnStore, btnScores, btnSettings, btnAdmin, btnQuit };

        // DB status label with retry button
        JPanel dbPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        dbPanel.setOpaque(false);
        lblDbStatus = new JLabel("");
        lblDbStatus.setFont(new Font("Arial", Font.BOLD, 12));

        JButton btnRetryDb = createSmallButton("Retry DB");
        btnRetryDb.addActionListener(e -> {
            database.DatabaseManager.retryConnection();
            refreshProfile();
        });

        dbPanel.add(lblDbStatus);
        dbPanel.add(btnRetryDb);

        add(Box.createVerticalStrut(20));
        add(profileHeader);
        add(Box.createVerticalStrut(10));
        add(dbPanel);
        add(Box.createVerticalStrut(20));
        add(titleLabel);
        add(Box.createVerticalStrut(40));
        
        for (JButton btn : menuButtons) {
            add(btn);
            add(Box.createVerticalStrut(15));
        }

        setupKeyBindings();
        refreshProfile();
        updateButtonSelection();
    }
    
    private void setupKeyBindings() {
        InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();
        
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "up");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "down");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
        
        am.put("up", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedButtonIndex--;
                if (selectedButtonIndex < 0) selectedButtonIndex = menuButtons.length - 1;
                updateButtonSelection();
            }
        });
        
        am.put("down", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedButtonIndex++;
                if (selectedButtonIndex >= menuButtons.length) selectedButtonIndex = 0;
                updateButtonSelection();
            }
        });
        
        am.put("enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuButtons[selectedButtonIndex].doClick();
            }
        });
    }
    
    private void updateButtonSelection() {
        for (int i = 0; i < menuButtons.length; i++) {
            if (i == selectedButtonIndex) {
                menuButtons[i].getModel().setRollover(true);
            } else {
                menuButtons[i].getModel().setRollover(false);
            }
            menuButtons[i].repaint();
        }
    }

    private void changeProfileDialog() {
        String currentName = mainFrame.getActivePlayer() != null ? mainFrame.getActivePlayer().getUsername() : "Player 1";
        String chosen = promptForPlayerName("Change Profile", currentName);
        if (chosen != null) {
            Player p = ensurePlayerExists(chosen);
            mainFrame.setActivePlayer(p);
        }
    }
    
    private String promptForPlayerName(String title, String defaultName) {
        if (!database.DatabaseManager.isConnected()) {
            database.DatabaseManager.retryConnection();
        }
        
        ScoreDAO dao = new ScoreDAOImpl();
        List<Player> players = dao.getAllPlayers();
        
        if (players.isEmpty()) {
            String input = JOptionPane.showInputDialog(mainFrame, "Enter username for " + title + ":", defaultName);
            if (input != null && !input.trim().isEmpty()) {
                ensurePlayerExists(input.trim());
                return input.trim();
            }
            return null;
        }

        String[] playerNames = players.stream().map(Player::getUsername).toArray(String[]::new);
        String[] options = new String[playerNames.length + 1];
        System.arraycopy(playerNames, 0, options, 0, playerNames.length);
        options[options.length - 1] = "-- Create New Player --";

        String selected = (String) JOptionPane.showInputDialog(
            mainFrame, 
            "Select " + title + ":", 
            title, 
            JOptionPane.PLAIN_MESSAGE, 
            null, 
            options, 
            defaultName != null ? defaultName : playerNames[0]
        );

        if (selected != null) {
            if (selected.equals("-- Create New Player --")) {
                String input = JOptionPane.showInputDialog(mainFrame, "Enter new username for " + title + ":");
                if (input != null && !input.trim().isEmpty()) {
                    ensurePlayerExists(input.trim());
                    return input.trim();
                }
            } else {
                ensurePlayerExists(selected);
                return selected;
            }
        }
        return null;
    }
    
    private Player ensurePlayerExists(String newName) {
        ScoreDAO dao = new ScoreDAOImpl();
        try {
            Player p = dao.getPlayer(newName);
            if (p == null) {
                p = new Player(0, newName, 0, "default");
                dao.savePlayer(p);
                Player loaded = dao.getPlayer(newName);
                if (loaded != null) return loaded;
                return p;
            }
            return p;
        } catch (Exception e) {
            return new Player(0, newName, 0, "default");
        }
    }

    public void refreshProfile() {
        Player player = mainFrame.getActivePlayer();
        boolean connected = database.DatabaseManager.isConnected();

        if (lblDbStatus != null) {
            if (connected) {
                lblDbStatus.setText("● Base de données connectée (MySQL)");
                lblDbStatus.setForeground(Constants.COLOR_SNAKE_1_HEAD);
            } else {
                lblDbStatus.setText("⚠ Base de données hors ligne – Scores non enregistrés");
                lblDbStatus.setForeground(Constants.COLOR_FOOD_POS);
            }
        }

        if (player != null) {
            lblPlayerName.setText("Player: " + player.getUsername());
            lblPlayerCoins.setText("💰 " + player.getTotalCoins());

            if (connected) {
                lblPlayerBest.setText("🏆 Best: ...");
                final String username = player.getUsername();
                new SwingWorker<Integer, Void>() {
                    @Override
                    protected Integer doInBackground() {
                        return new ScoreDAOImpl().getBestScore(username);
                    }
                    @Override
                    protected void done() {
                        try {
                            lblPlayerBest.setText("🏆 Best: " + get());
                        } catch (Exception ex) {
                            lblPlayerBest.setText("🏆 Best: ?");
                        }
                    }
                }.execute();
            } else {
                lblPlayerBest.setText("🏆 Best: -- (hors ligne)");
            }
        } else {
            lblPlayerName.setText("Player: ---");
            lblPlayerCoins.setText("💰 0");
            lblPlayerBest.setText("🏆 Best: 0");
        }
        revalidate();
        repaint();
    }

    private JButton createSmallButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) g2.setColor(Constants.COLOR_SNAKE_1_HEAD.darker());
                else g2.setColor(Constants.COLOR_BACKGROUND.brighter());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Arial", Font.BOLD, 11));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 25));
        return btn;
    }

    private JButton createModernButton(String text, Color accentColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) g2.setColor(accentColor.darker());
                else g2.setColor(Constants.COLOR_UI_PANEL);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(accentColor);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btn.setFont(Constants.FONT_MENU);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setAlignmentX(CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(350, 55));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { 
                btn.setForeground(accentColor); 
                // Update selection index when hovering with mouse
                for(int i = 0; i < menuButtons.length; i++) {
                    if (menuButtons[i] == btn) {
                        selectedButtonIndex = i;
                        updateButtonSelection();
                        break;
                    }
                }
            }
            public void mouseExited(MouseEvent e) { btn.setForeground(Color.WHITE); }
        });
        return btn;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        GradientPaint gp = new GradientPaint(0, 0, Constants.COLOR_BACKGROUND, 0, getHeight(), new Color(5, 5, 10));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
    }
}
