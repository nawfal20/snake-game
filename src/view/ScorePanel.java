package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import database.ScoreDAO;
import database.ScoreDAOImpl;
import model.Score;
import utils.Constants;

public class ScorePanel extends JPanel {
    private MainFrame mainFrame;
    private JTable scoreTable;
    private DefaultTableModel tableModel;

    public ScorePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("TOP PLAYERS", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(Constants.COLOR_FOOD_POS);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"Player", "Score", "Level", "Coins", "Duration (s)", "Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        scoreTable = new JTable(tableModel);
        scoreTable.setFillsViewportHeight(true);
        scoreTable.setBackground(Constants.COLOR_UI_PANEL);
        scoreTable.setForeground(Color.WHITE);
        scoreTable.setFont(new Font("Arial", Font.PLAIN, 16));
        scoreTable.setRowHeight(35);
        scoreTable.setShowGrid(false);
        scoreTable.setIntercellSpacing(new Dimension(0, 0));

        // Header styling
        JTableHeader header = scoreTable.getTableHeader();
        header.setBackground(Constants.COLOR_BACKGROUND.darker());
        header.setForeground(Constants.COLOR_TEXT_MUTED);
        header.setFont(new Font("Arial", Font.BOLD, 18));
        header.setPreferredSize(new Dimension(100, 40));
        
        // Center text in cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < scoreTable.getColumnCount(); i++) {
            scoreTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(scoreTable);
        scrollPane.getViewport().setBackground(Constants.COLOR_BACKGROUND);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50));
        add(scrollPane, BorderLayout.CENTER);

        JButton btnBack = createModernButton("Back to Menu", Constants.COLOR_FOOD_POS);
        btnBack.addActionListener(e -> mainFrame.showPanel("MENU"));
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        bottomPanel.add(btnBack);
        add(bottomPanel, BorderLayout.SOUTH);

        loadScores();
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

    public void loadScores() {
        tableModel.setRowCount(0);
        ScoreDAO dao = new ScoreDAOImpl();
        List<Score> scores = dao.getTopScores(15); // Show top 15 scores
        for (Score s : scores) {
            tableModel.addRow(new Object[]{
                s.getPlayerName(), 
                s.getScore(), 
                s.getLevel(), 
                s.getCoinsCollected(), 
                s.getDuration(), 
                s.getCreatedAt()
            });
        }
    }
}
