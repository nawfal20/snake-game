package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.Player;
import model.Store;
import database.ScoreDAO;
import database.ScoreDAOImpl;
import utils.Constants;

public class StorePanel extends JPanel {
    private MainFrame mainFrame;
    private JLabel lblPlayerCoins;

    public StorePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("STORE / SHOP", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(Constants.COLOR_COIN);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 5, 0));
        headerPanel.add(titleLabel);

        Player player = mainFrame.getActivePlayer();
        int playerCoins = player != null ? player.getTotalCoins() : 0;
        lblPlayerCoins = new JLabel("Your Balance: 💰 " + playerCoins + " Coins", JLabel.CENTER);
        lblPlayerCoins.setFont(new Font("Arial", Font.BOLD, 18));
        lblPlayerCoins.setForeground(Color.WHITE);
        headerPanel.add(lblPlayerCoins);

        add(headerPanel, BorderLayout.NORTH);

        JPanel itemsPanel = new JPanel(new GridLayout(0, 3, 30, 30));
        itemsPanel.setOpaque(false);
        itemsPanel.setBorder(new EmptyBorder(25, 60, 25, 60));

        // Load store items
        Store store = new Store();
        ScoreDAO dao = new ScoreDAOImpl();
        
        List<String> ownedSkins = null;
        if (player != null && player.getId() > 0) {
            ownedSkins = dao.getOwnedSkins(player.getId());
        }

        for (Store.StoreItem item : store.getItems()) {
            itemsPanel.add(createItemCard(item, ownedSkins));
        }

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.getViewport().setBackground(Constants.COLOR_BACKGROUND);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);

        JButton btnBack = createModernButton("Back to Menu", Constants.COLOR_COIN);
        btnBack.addActionListener(e -> mainFrame.showPanel("MENU"));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(15, 0, 20, 0));
        bottomPanel.add(btnBack);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createItemCard(Store.StoreItem item, List<String> ownedSkins) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Constants.COLOR_UI_PANEL);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(Constants.COLOR_COIN);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel nameLabel = new JLabel(item.getName(), JLabel.CENTER);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        card.add(nameLabel, BorderLayout.NORTH);

        JLabel typeLabel = new JLabel(item.getType(), JLabel.CENTER);
        typeLabel.setForeground(Constants.COLOR_TEXT_MUTED);
        typeLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        card.add(typeLabel, BorderLayout.CENTER);

        // Check button action (Buy / Equip / Equipped)
        Player player = mainFrame.getActivePlayer();
        JButton btnAction;

        if (player == null) {
            btnAction = createModernButton(item.getPrice() + " Coins", Constants.COLOR_COIN);
            btnAction.setEnabled(false);
        } else {
            String selectedSkin = player.getSelectedSkin();
            boolean isOwned = item.getType().equalsIgnoreCase("EFFECT") 
                    || (ownedSkins != null && ownedSkins.contains(item.getName()))
                    || item.getName().equalsIgnoreCase("default");

            if (isOwned) {
                if (selectedSkin != null && selectedSkin.equalsIgnoreCase(item.getName())) {
                    btnAction = createModernButton("Equipped", Constants.COLOR_SNAKE_1_HEAD);
                    btnAction.setEnabled(false);
                } else if (item.getType().equalsIgnoreCase("EFFECT")) {
                    btnAction = createModernButton("Buy (" + item.getPrice() + "c)", Constants.COLOR_COIN);
                    btnAction.addActionListener(e -> buyItem(item));
                } else {
                    btnAction = createModernButton("Equip", Color.WHITE);
                    btnAction.addActionListener(e -> equipSkin(item.getName()));
                }
            } else {
                btnAction = createModernButton(item.getPrice() + " Coins", Constants.COLOR_COIN);
                btnAction.addActionListener(e -> buyItem(item));
            }
        }

        btnAction.setPreferredSize(new Dimension(100, 35));
        btnAction.setFont(new Font("Arial", Font.BOLD, 14));
        card.add(btnAction, BorderLayout.SOUTH);

        return card;
    }

    private void equipSkin(String skinName) {
        Player player = mainFrame.getActivePlayer();
        if (player != null && player.getId() > 0) {
            ScoreDAO dao = new ScoreDAOImpl();
            dao.updateSelectedSkin(player.getId(), skinName);
            player.setSelectedSkin(skinName);
            mainFrame.showPanel("STORE"); // Refresh view
        } else {
            // Guest profile equip local
            if (player != null) {
                player.setSelectedSkin(skinName);
                mainFrame.showPanel("STORE");
            }
        }
    }

    private void buyItem(Store.StoreItem item) {
        Player player = mainFrame.getActivePlayer();
        if (player == null) return;

        if (player.getTotalCoins() < item.getPrice()) {
            JOptionPane.showMessageDialog(this, "Not enough coins! / Pas assez de pièces !", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (item.getType().equalsIgnoreCase("EFFECT")) {
            // Effects are consumable: deduct coins + activate in game
            player.setTotalCoins(player.getTotalCoins() - item.getPrice());
            
            // Update coins in DB if connected
            if (player.getId() > 0) {
                ScoreDAO dao = new ScoreDAOImpl();
                dao.updatePlayerCoins(player.getUsername(), player.getTotalCoins());
            }
            
            // Activate the effect in the game controller
            controller.GameController gc = mainFrame.getGameController();
            gc.activateEffect(item.getName());
            
            JOptionPane.showMessageDialog(this, 
                "✅ " + item.getName() + " activated!\nEffect will be active in your next/current game.", 
                "Effect Activated", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.showPanel("STORE"); // Refresh view
            return;
        }

        // SKIN purchase flow
        if (player.getId() <= 0) {
            // Guest user local buy
            player.setTotalCoins(player.getTotalCoins() - item.getPrice());
            JOptionPane.showMessageDialog(this, "Bought! (Guest Profile / Local only)", "Success", JOptionPane.INFORMATION_MESSAGE);
            equipSkin(item.getName());
            return;
        }

        ScoreDAO dao = new ScoreDAOImpl();
        boolean success = dao.buySkin(player.getId(), item.getId(), item.getPrice());
        if (success) {
            player.setTotalCoins(player.getTotalCoins() - item.getPrice());
            JOptionPane.showMessageDialog(this, "Skin successfully bought and unlocked! / Skin acheté et débloqué !", "Success", JOptionPane.INFORMATION_MESSAGE);
            equipSkin(item.getName());
        } else {
            JOptionPane.showMessageDialog(this, "Purchase failed! Database error.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton createModernButton(String text, Color accentColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (!isEnabled()) {
                    g2.setColor(new Color(50, 50, 50));
                } else if (getModel().isRollover()) {
                    g2.setColor(accentColor.darker());
                } else {
                    g2.setColor(Constants.COLOR_UI_PANEL.darker());
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(isEnabled() ? accentColor : Color.GRAY);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 15, 15);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btn.setFont(Constants.FONT_MENU);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(200, 50));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if (btn.isEnabled()) btn.setForeground(accentColor); }
            public void mouseExited(MouseEvent e) { btn.setForeground(Color.WHITE); }
        });
        return btn;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gp = new GradientPaint(0, 0, Constants.COLOR_BACKGROUND, 0, getHeight(), new Color(10, 10, 15));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}
