package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Properties;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import utils.Constants;

public class AdminLoginPanel extends JPanel {
    private static final String PROP_FILE = "admin.properties";
    private static final String PROP_KEY = "admin.password";
    
    private MainFrame mainFrame;
    private JComboBox<String> devComboBox;
    private JPasswordField txtPassword;
    private JPasswordField txtChoosePassword;
    private JPasswordField txtConfirmPassword;
    
    private JPanel cardContentPanel;
    private JLabel subtitleLabel;
    
    private boolean isSetupMode = false;
    
    public AdminLoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        
        checkSetupMode();
        initComponents();
    }
    
    private void checkSetupMode() {
        File f = new File(PROP_FILE);
        if (!f.exists()) {
            isSetupMode = true;
            return;
        }
        
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(f)) {
            props.load(fis);
            String pwd = props.getProperty(PROP_KEY);
            isSetupMode = (pwd == null || pwd.trim().isEmpty());
        } catch (IOException e) {
            isSetupMode = true;
        }
    }
    
    private void initComponents() {
        removeAll();
        
        // Inner Glassmorphism Card
        JPanel loginCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Glass-like semi-transparent dark panel
                g2.setColor(new Color(25, 20, 38, 220));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                // Neon glowing purple border
                g2.setColor(new Color(180, 100, 255));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
                g2.dispose();
            }
        };
        loginCard.setOpaque(false);
        loginCard.setLayout(new BorderLayout());
        loginCard.setPreferredSize(new Dimension(450, 480));
        loginCard.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // Header in the card
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("⚙ DEVELOPER PORTAL", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(200, 150, 255));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        
        subtitleLabel = new JLabel(isSetupMode ? "Setup Portal Access Password" : "Secure Authentication Required", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 13));
        subtitleLabel.setForeground(Constants.COLOR_TEXT_MUTED);
        subtitleLabel.setAlignmentX(CENTER_ALIGNMENT);
        
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(subtitleLabel);
        headerPanel.add(Box.createVerticalStrut(25));
        
        loginCard.add(headerPanel, BorderLayout.NORTH);
        
        // Form Content
        cardContentPanel = new JPanel();
        cardContentPanel.setLayout(new BoxLayout(cardContentPanel, BoxLayout.Y_AXIS));
        cardContentPanel.setOpaque(false);
        
        if (isSetupMode) {
            setupRegisterForm(cardContentPanel);
        } else {
            setupLoginForm(cardContentPanel);
        }
        
        loginCard.add(cardContentPanel, BorderLayout.CENTER);
        
        // Footer buttons
        JPanel footerPanel = new JPanel(new GridLayout(2, 1, 0, 12));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        JButton btnSubmit = createModernButton(isSetupMode ? "🔑 Set Password & Access" : "🔓 Authenticate", new Color(180, 100, 255));
        btnSubmit.addActionListener(e -> {
            if (isSetupMode) {
                handleSetup();
            } else {
                handleLogin();
            }
        });
        
        JButton btnCancel = createModernButton("← Back to Menu", Color.GRAY);
        btnCancel.addActionListener(e -> mainFrame.showPanel("MENU"));
        
        footerPanel.add(btnSubmit);
        footerPanel.add(btnCancel);
        
        loginCard.add(footerPanel, BorderLayout.SOUTH);
        
        add(loginCard);
        
        revalidate();
        repaint();
    }
    
    private void setupLoginForm(JPanel panel) {
        panel.removeAll();
        
        // Developer selection
        JLabel lblDev = new JLabel("Developer:");
        lblDev.setFont(new Font("Arial", Font.BOLD, 14));
        lblDev.setForeground(Color.WHITE);
        lblDev.setAlignmentX(LEFT_ALIGNMENT);
        
        devComboBox = new JComboBox<>(new String[]{"Ayoub", "Naoufal", "Houssam"});
        devComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        devComboBox.setForeground(Color.WHITE);
        devComboBox.setBackground(new Color(40, 35, 55));
        devComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        devComboBox.setAlignmentX(LEFT_ALIGNMENT);
        devComboBox.setBorder(BorderFactory.createLineBorder(new Color(100, 80, 140), 1));
        
        // Password field
        JLabel lblPass = new JLabel("Common Password:");
        lblPass.setFont(new Font("Arial", Font.BOLD, 14));
        lblPass.setForeground(Color.WHITE);
        lblPass.setAlignmentX(LEFT_ALIGNMENT);
        
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 16));
        txtPassword.setForeground(Color.WHITE);
        txtPassword.setBackground(new Color(30, 25, 45));
        txtPassword.setCaretColor(Color.WHITE);
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtPassword.setAlignmentX(LEFT_ALIGNMENT);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 80, 140), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        panel.add(lblDev);
        panel.add(Box.createVerticalStrut(6));
        panel.add(devComboBox);
        panel.add(Box.createVerticalStrut(20));
        panel.add(lblPass);
        panel.add(Box.createVerticalStrut(6));
        panel.add(txtPassword);
    }
    
    private void setupRegisterForm(JPanel panel) {
        panel.removeAll();
        
        JLabel lblInfo = new JLabel("<html><center>No password set yet. Please choose a<br>common password for Ayoub, Naoufal and Houssam.</center></html>");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 13));
        lblInfo.setForeground(new Color(220, 200, 255));
        lblInfo.setAlignmentX(CENTER_ALIGNMENT);
        lblInfo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JLabel lblChoose = new JLabel("Choose Common Password:");
        lblChoose.setFont(new Font("Arial", Font.BOLD, 14));
        lblChoose.setForeground(Color.WHITE);
        lblChoose.setAlignmentX(LEFT_ALIGNMENT);
        
        txtChoosePassword = new JPasswordField();
        txtChoosePassword.setFont(new Font("Arial", Font.PLAIN, 16));
        txtChoosePassword.setForeground(Color.WHITE);
        txtChoosePassword.setBackground(new Color(30, 25, 45));
        txtChoosePassword.setCaretColor(Color.WHITE);
        txtChoosePassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtChoosePassword.setAlignmentX(LEFT_ALIGNMENT);
        txtChoosePassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 80, 140), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        JLabel lblConfirm = new JLabel("Confirm Common Password:");
        lblConfirm.setFont(new Font("Arial", Font.BOLD, 14));
        lblConfirm.setForeground(Color.WHITE);
        lblConfirm.setAlignmentX(LEFT_ALIGNMENT);
        
        txtConfirmPassword = new JPasswordField();
        txtConfirmPassword.setFont(new Font("Arial", Font.PLAIN, 16));
        txtConfirmPassword.setForeground(Color.WHITE);
        txtConfirmPassword.setBackground(new Color(30, 25, 45));
        txtConfirmPassword.setCaretColor(Color.WHITE);
        txtConfirmPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtConfirmPassword.setAlignmentX(LEFT_ALIGNMENT);
        txtConfirmPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 80, 140), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        panel.add(lblInfo);
        panel.add(lblChoose);
        panel.add(Box.createVerticalStrut(6));
        panel.add(txtChoosePassword);
        panel.add(Box.createVerticalStrut(15));
        panel.add(lblConfirm);
        panel.add(Box.createVerticalStrut(6));
        panel.add(txtConfirmPassword);
    }
    
    private void handleSetup() {
        String pass1 = new String(txtChoosePassword.getPassword()).trim();
        String pass2 = new String(txtConfirmPassword.getPassword()).trim();
        
        if (pass1.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!pass1.equals(pass2)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (pass1.length() < 4) {
            JOptionPane.showMessageDialog(this, "Password must be at least 4 characters long!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String hashed = hashPassword(pass1);
        
        Properties props = new Properties();
        File f = new File(PROP_FILE);
        if (f.exists()) {
            try (FileInputStream fis = new FileInputStream(f)) {
                props.load(fis);
            } catch (IOException ignored) {}
        }
        
        props.setProperty(PROP_KEY, hashed);
        
        try (FileOutputStream fos = new FileOutputStream(PROP_FILE)) {
            props.store(fos, "Snake Game Pro - Admin Credentials");
            JOptionPane.showMessageDialog(this, "Common Password set successfully! Access granted.", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            isSetupMode = false;
            mainFrame.showPanel("ADMIN");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving settings: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleLogin() {
        String entered = new String(txtPassword.getPassword());
        String selectedDev = (String) devComboBox.getSelectedItem();
        
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(PROP_FILE)) {
            props.load(fis);
            String storedHash = props.getProperty(PROP_KEY);
            String enteredHash = hashPassword(entered);
            
            if (storedHash != null && storedHash.equals(enteredHash)) {
                JOptionPane.showMessageDialog(this, "Welcome back, " + selectedDev + "! Access granted.", "Authenticated", JOptionPane.INFORMATION_MESSAGE);
                mainFrame.showPanel("ADMIN");
            } else {
                JOptionPane.showMessageDialog(this, "Incorrect password!", "Authentication Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading credentials file. Resetting to setup mode.", "Error", JOptionPane.ERROR_MESSAGE);
            isSetupMode = true;
            initComponents();
        }
    }
    
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            return password; // Fallback
        }
    }
    
    public void resetFields() {
        checkSetupMode();
        initComponents();
        if (txtPassword != null) txtPassword.setText("");
        if (txtChoosePassword != null) txtChoosePassword.setText("");
        if (txtConfirmPassword != null) txtConfirmPassword.setText("");
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
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
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
        GradientPaint gp = new GradientPaint(0, 0, Constants.COLOR_BACKGROUND, 0, getHeight(), new Color(15, 10, 25));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}
