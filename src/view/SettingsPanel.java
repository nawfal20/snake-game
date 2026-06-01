package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import database.ScoreDAO;
import database.ScoreDAOImpl;
import model.Store;
import utils.Constants;

public class SettingsPanel extends JPanel {
    private MainFrame mainFrame;

    public SettingsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setOpaque(false);

        JLabel titleLabel = new JLabel("SETTINGS & HELP", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(Constants.COLOR_SNAKE_2_HEAD);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(10, 50, 10, 50));

        // Instructions
        JPanel instructionsPanel = createSectionPanel("How to Play");
        JTextArea txtInstructions = new JTextArea(
            "• Use ARROW KEYS to move your snake.\n" +
            "• In 2-Player mode, Player 1 uses arrows, Player 2 uses W/A/S/D.\n" +
            "• Eat red apples to grow and earn points.\n" +
            "• Avoid purple poison, walls, and other snakes.\n" +
            "• The Level and Speed increase every 5 points.\n" +
            "• Collect Gold Coins to buy skins in the Store!"
        );
        txtInstructions.setFont(new Font("Arial", Font.PLAIN, 16));
        txtInstructions.setForeground(Color.WHITE);
        txtInstructions.setOpaque(false);
        txtInstructions.setEditable(false);
        instructionsPanel.add(txtInstructions);
        contentPanel.add(instructionsPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Speed Settings
        JPanel speedPanel = createSectionPanel("Game Speed");
        speedPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel lblSpeed = new JLabel("Initial Speed (Lower is faster): ");
        lblSpeed.setForeground(Color.WHITE);
        lblSpeed.setFont(new Font("Arial", Font.PLAIN, 16));
        
        JSlider speedSlider = new JSlider(30, 200, Constants.GAME_SPEED_NORMAL);
        speedSlider.setOpaque(false);
        speedSlider.setForeground(Color.WHITE);
        speedSlider.setMajorTickSpacing(50);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.addChangeListener(e -> {
            Constants.GAME_SPEED_NORMAL = speedSlider.getValue();
        });
        
        speedPanel.add(lblSpeed);
        speedPanel.add(speedSlider);
        contentPanel.add(speedPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Skin Prices
        JPanel skinPanel = createSectionPanel("Admin: Change Skin Prices");
        skinPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        ScoreDAO dao = new ScoreDAOImpl();
        List<Store.StoreItem> items = dao.getStoreItems();
        JComboBox<String> skinComboBox = new JComboBox<>();
        for (Store.StoreItem item : items) {
            if (item.getType().equals("SKIN")) {
                skinComboBox.addItem(item.getId() + ": " + item.getName() + " (" + item.getPrice() + " coins)");
            }
        }
        
        JTextField txtNewPrice = new JTextField(5);
        JButton btnUpdatePrice = new JButton("Update Price");
        btnUpdatePrice.addActionListener(e -> {
            try {
                int newPrice = Integer.parseInt(txtNewPrice.getText().trim());
                if (newPrice >= 0) {
                    String selected = (String) skinComboBox.getSelectedItem();
                    if (selected != null) {
                        int itemId = Integer.parseInt(selected.split(":")[0]);
                        dao.updateSkinPrice(itemId, newPrice);
                        JOptionPane.showMessageDialog(this, "Price updated successfully!");
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for the price.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        skinPanel.add(new JLabel("Select Skin: ") {{ setForeground(Color.WHITE); }});
        skinPanel.add(skinComboBox);
        skinPanel.add(new JLabel("New Price: ") {{ setForeground(Color.WHITE); }});
        skinPanel.add(txtNewPrice);
        skinPanel.add(btnUpdatePrice);
        
        if (database.DatabaseManager.isConnected()) {
            contentPanel.add(skinPanel);
        }

        add(contentPanel, BorderLayout.CENTER);

        JButton btnBack = createModernButton("Back to Menu", Constants.COLOR_SNAKE_2_HEAD);
        btnBack.addActionListener(e -> mainFrame.showPanel("MENU"));
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        bottomPanel.add(btnBack);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Constants.COLOR_UI_PANEL, 2), 
            title, 
            0, 0, 
            new Font("Arial", Font.BOLD, 18), 
            Constants.COLOR_SNAKE_1_HEAD
        ));
        return panel;
    }

    private JButton createModernButton(String text, Color accentColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) g2.setColor(accentColor.darker());
                else g2.setColor(Constants.COLOR_UI_PANEL);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(accentColor);
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
            public void mouseEntered(MouseEvent e) { btn.setForeground(accentColor); }
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
