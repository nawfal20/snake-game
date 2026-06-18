package view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import java.awt.CardLayout;
import controller.GameController;
import model.Player;
import database.ScoreDAO;
import database.ScoreDAOImpl;
import utils.Constants;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    private MenuPanel menuPanel;
    private GamePanel gamePanel;
    private ScorePanel scorePanel;
    private StorePanel storePanel;
    private AdminLoginPanel adminLoginPanel;
    private AdminPanel adminPanel;
    private WelcomePanel welcomePanel;

    private GameController gameController;
    private Player activePlayer;

    public MainFrame() {
        setTitle(Constants.TITLE);
        setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Fermeture propre : on ferme la connexion DB avant de quitter
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                database.DatabaseManager.closeConnection();
                dispose();
                System.exit(0);
            }
        });

        gameController = new GameController();
        gameController.setMainFrame(this);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        menuPanel = new MenuPanel(this);
        gamePanel = new GamePanel(this, gameController);
        scorePanel = new ScorePanel(this);
        storePanel = new StorePanel(this);
        adminLoginPanel = new AdminLoginPanel(this);
        adminPanel = new AdminPanel(this);
        welcomePanel = new WelcomePanel(this);

        gameController.setGamePanel(gamePanel);

        mainPanel.add(welcomePanel, "WELCOME");
        mainPanel.add(menuPanel, "MENU");
        mainPanel.add(gamePanel, "GAME");
        mainPanel.add(scorePanel, "SCORES");
        mainPanel.add(storePanel, "STORE");
        mainPanel.add(adminLoginPanel, "ADMIN_LOGIN");
        mainPanel.add(adminPanel, "ADMIN");

        add(mainPanel);

        addKeyListener(gameController.getKeyboardHandler());
        setFocusable(true);

        showPanel("WELCOME");

        // Charger le joueur par défaut en arrière-plan pour ne pas geler le démarrage
        loadDefaultPlayerAsync();
    }

    /**
     * CORRECTION : Chargement du joueur en arrière-plan (SwingWorker)
     * pour que la fenêtre s'affiche immédiatement même si MySQL est lent.
     */
    private void loadDefaultPlayerAsync() {
        new SwingWorker<Player, Void>() {
            @Override
            protected Player doInBackground() {
                ScoreDAO dao = new ScoreDAOImpl();
                try {
                    Player p = dao.getPlayer("Player1");
                    if (p == null) {
                        p = new Player(0, "Player1", 0, "default");
                        dao.savePlayer(p);
                        Player loaded = dao.getPlayer("Player1");
                        if (loaded != null) p = loaded;
                    }
                    return p;
                } catch (Exception e) {
                    System.err.println("Base de données hors ligne. Mode invité activé.");
                    return new Player(0, "Player1", 0, "default");
                }
            }

            @Override
            protected void done() {
                try {
                    setActivePlayer(get());
                } catch (Exception e) {
                    setActivePlayer(new Player(0, "Player1", 0, "default"));
                }
            }
        }.execute();
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(Player player) {
        this.activePlayer = player;
        this.gameController.setActivePlayer(player);
        if (menuPanel != null) {
            menuPanel.refreshProfile();
        }
    }

    public void showPanel(String panelName) {
        if (panelName.equals("STORE")) {
            // CORRECTION : remove proprement l'ancien StorePanel avant d'en créer un nouveau
            mainPanel.remove(storePanel);
            storePanel = new StorePanel(this);
            mainPanel.add(storePanel, "STORE");
            mainPanel.revalidate();
        } else if (panelName.equals("SCORES")) {
            if (scorePanel != null) {
                scorePanel.loadScores();
            }
        } else if (panelName.equals("MENU")) {
            if (menuPanel != null) {
                menuPanel.refreshProfile();
            }
        } else if (panelName.equals("ADMIN")) {
            // Recreate AdminPanel to refresh prices
            mainPanel.remove(adminPanel);
            adminPanel = new AdminPanel(this);
            mainPanel.add(adminPanel, "ADMIN");
            mainPanel.revalidate();
        } else if (panelName.equals("ADMIN_LOGIN")) {
            if (adminLoginPanel != null) {
                adminLoginPanel.resetFields();
            }
        }

        // Stop welcome panel animations when leaving it
        if (!panelName.equals("WELCOME") && welcomePanel != null) {
            welcomePanel.stopAnimations();
        }

        cardLayout.show(mainPanel, panelName);
        if (panelName.equals("GAME")) {
            javax.swing.SwingUtilities.invokeLater(this::requestFocusInWindow);
        }
    }

    public void startGame(boolean isMultiplayer, String player2Name) {
        showPanel("GAME");
        gameController.startGame(isMultiplayer, player2Name);
    }

    public GameController getGameController() {
        return gameController;
    }
}
