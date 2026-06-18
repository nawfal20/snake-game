package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.Store;
import utils.Constants;

public class AdminPanel extends JPanel {
    private MainFrame mainFrame;

    public AdminPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("⚙ ADMIN — Price Manager", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(200, 150, 255));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 5, 0));
        headerPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Modify prices for all store items", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        subtitleLabel.setForeground(Constants.COLOR_TEXT_MUTED);
        headerPanel.add(subtitleLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Items list with price editors
        Store store = new Store();
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setOpaque(false);
        itemsPanel.setBorder(new EmptyBorder(15, 80, 15, 80));

        // Table header
        JPanel tableHeader = createTableRow("ITEM NAME", "TYPE", "CURRENT PRICE", null, -1, null);
        tableHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        itemsPanel.add(tableHeader);
        itemsPanel.add(Box.createVerticalStrut(5));

        // Add separator
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(60, 60, 80));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        itemsPanel.add(sep);
        itemsPanel.add(Box.createVerticalStrut(10));

        Map<Integer, JTextField> priceFields = new HashMap<>();

        for (Store.StoreItem item : store.getItems()) {
            JTextField priceField = new JTextField(String.valueOf(item.getPrice()), 6);
            priceField.setFont(new Font("Arial", Font.BOLD, 16));
            priceField.setForeground(Constants.COLOR_COIN);
            priceField.setBackground(new Color(35, 35, 50));
            priceField.setCaretColor(Color.WHITE);
            priceField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 100), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            priceField.setHorizontalAlignment(JTextField.CENTER);
            priceFields.put(item.getId(), priceField);

            JPanel row = createTableRow(item.getName(), item.getType(), null, priceField, item.getId(), store);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            itemsPanel.add(row);
            itemsPanel.add(Box.createVerticalStrut(8));
        }

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.getViewport().setBackground(Constants.COLOR_BACKGROUND);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 20, 0));

        JButton btnSaveAll = createModernButton("💾 Save All Prices", new Color(0, 200, 100));
        btnSaveAll.addActionListener(e -> {
            int saved = 0;
            for (Store.StoreItem item : store.getItems()) {
                JTextField field = priceFields.get(item.getId());
                if (field != null) {
                    try {
                        int newPrice = Integer.parseInt(field.getText().trim());
                        if (newPrice >= 0 && newPrice != item.getPrice()) {
                            store.updateItemPrice(item.getId(), newPrice);
                            saved++;
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, 
                            "Invalid price for " + item.getName() + "! Must be a number.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
            JOptionPane.showMessageDialog(this,
                saved + " price(s) updated successfully!",
                "Saved", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnDefaultPrices = createModernButton("↩ Default Prices", new Color(255, 140, 0));
        btnDefaultPrices.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to reset all prices to their default values?\n" +
                "Voulez-vous vraiment réinitialiser tous les prix par défaut ?",
                "Reset Prices", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                for (Store.StoreItem item : store.getItems()) {
                    int defaultPrice = Store.getDefaultPrice(item.getId());
                    store.updateItemPrice(item.getId(), defaultPrice);
                    JTextField field = priceFields.get(item.getId());
                    if (field != null) {
                        field.setText(String.valueOf(defaultPrice));
                    }
                }
                JOptionPane.showMessageDialog(this,
                    "All prices reset to default successfully!\n" +
                    "Tous les prix ont été réinitialisés par défaut !",
                    "Reset Complete", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton btnBack = createModernButton("← Back to Menu", Constants.COLOR_COIN);
        btnBack.addActionListener(e -> mainFrame.showPanel("MENU"));

        bottomPanel.add(btnSaveAll);
        bottomPanel.add(btnDefaultPrices);
        bottomPanel.add(btnBack);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createTableRow(String name, String type, String priceText, JTextField priceField, int itemId, Store store) {
        JPanel row = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (itemId >= 0) {
                    g2.setColor(new Color(25, 25, 40, 200));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                }
                g2.dispose();
            }
        };
        row.setOpaque(false);
        row.setLayout(new GridLayout(1, 4, 15, 0));
        row.setBorder(new EmptyBorder(8, 15, 8, 15));

        // Name
        JLabel lblName = new JLabel(name);
        lblName.setFont(new Font("Arial", itemId < 0 ? Font.BOLD : Font.PLAIN, itemId < 0 ? 13 : 16));
        lblName.setForeground(itemId < 0 ? Constants.COLOR_TEXT_MUTED : Color.WHITE);
        row.add(lblName);

        // Type
        JLabel lblType = new JLabel(type);
        lblType.setFont(new Font("Arial", Font.ITALIC, itemId < 0 ? 13 : 14));
        Color typeColor = Constants.COLOR_TEXT_MUTED;
        if (type != null && type.equalsIgnoreCase("SKIN")) typeColor = Constants.COLOR_SNAKE_1_HEAD;
        else if (type != null && type.equalsIgnoreCase("EFFECT")) typeColor = new Color(255, 165, 0);
        lblType.setForeground(typeColor);
        row.add(lblType);

        // Price
        if (priceField != null) {
            row.add(priceField);
        } else {
            JLabel lblPrice = new JLabel(priceText != null ? priceText : "");
            lblPrice.setFont(new Font("Arial", Font.BOLD, 13));
            lblPrice.setForeground(Constants.COLOR_TEXT_MUTED);
            row.add(lblPrice);
        }

        // Action button (individual save)
        if (itemId >= 0 && store != null) {
            JButton btnSave = createSmallSaveButton("Save");
            btnSave.addActionListener(e -> {
                try {
                    int newPrice = Integer.parseInt(priceField.getText().trim());
                    if (newPrice >= 0) {
                        store.updateItemPrice(itemId, newPrice);
                        JOptionPane.showMessageDialog(this, "Price updated!", "OK", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Price must be >= 0", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid number!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            row.add(btnSave);
        } else {
            JLabel lblAction = new JLabel(itemId < 0 ? "ACTION" : "");
            lblAction.setFont(new Font("Arial", Font.BOLD, 13));
            lblAction.setForeground(Constants.COLOR_TEXT_MUTED);
            row.add(lblAction);
        }

        return row;
    }

    private JButton createSmallSaveButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) g2.setColor(new Color(0, 180, 80));
                else g2.setColor(new Color(30, 60, 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(0, 200, 100));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createModernButton(String text, Color accentColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) g2.setColor(accentColor.darker());
                else g2.setColor(Constants.COLOR_UI_PANEL.darker());
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
        btn.setPreferredSize(new Dimension(250, 50));
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
        GradientPaint gp = new GradientPaint(0, 0, Constants.COLOR_BACKGROUND, 0, getHeight(), new Color(15, 10, 25));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}
