package utils;

import java.awt.Color;
import java.awt.Font;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Constants {
    // Window settings
    public static final String TITLE = "Snake Game Pro";
    public static final int WINDOW_WIDTH = 1000;
    public static final int WINDOW_HEIGHT = 700;

    // Game settings
    public static final int TILE_SIZE = 25;
    public static final int GRID_WIDTH = 800 / TILE_SIZE;  // 32
    public static final int GRID_HEIGHT = 600 / TILE_SIZE; // 24

    public static int GAME_SPEED_NORMAL = 120;
    public static int GAME_SPEED_FAST = 80;

    // Colors
    public static final Color COLOR_BACKGROUND = new Color(15, 15, 20);
    public static final Color COLOR_GRID = new Color(30, 30, 40);
    public static final Color COLOR_SNAKE_1_HEAD = new Color(0, 255, 128);
    public static final Color COLOR_SNAKE_1_BODY = new Color(0, 200, 100);
    public static final Color COLOR_SNAKE_2_HEAD = new Color(0, 191, 255);
    public static final Color COLOR_SNAKE_2_BODY = new Color(0, 150, 220);
    public static final Color COLOR_FOOD_POS = new Color(255, 50, 100);
    public static final Color COLOR_FOOD_NEG = new Color(138, 43, 226);
    public static final Color COLOR_COIN = new Color(255, 215, 0);
    public static final Color COLOR_UI_PANEL = new Color(25, 25, 35);
    public static final Color COLOR_TEXT_MUTED = new Color(150, 150, 170);

    // Fonts
    public static final Font FONT_TITLE = new Font("Arial", Font.BOLD, 48);
    public static final Font FONT_MENU = new Font("Arial", Font.BOLD, 24);
    public static final Font FONT_SCORE = new Font("Arial", Font.BOLD, 18);

    // =========================================================
    // CONFIGURATION BASE DE DONNÉES — chargée depuis db.properties
    // =========================================================
    // Ne jamais mettre vos identifiants directement ici.
    // Copiez db.properties.example → db.properties et remplissez vos valeurs.
    // db.properties est exclu du dépôt Git (.gitignore).
    // =========================================================
    public static final String DB_URL;
    public static final String DB_USER;
    public static final String DB_PASS;

    static {
        Properties props = new Properties();
        String dbUrl  = "jdbc:mysql://localhost:3306/snakegame?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String dbUser = "root";
        String dbPass = "";

        try (FileInputStream fis = new FileInputStream("db.properties")) {
            props.load(fis);
            dbUrl  = props.getProperty("db.url",  dbUrl);
            dbUser = props.getProperty("db.user", dbUser);
            dbPass = props.getProperty("db.pass", dbPass);
        } catch (IOException e) {
            System.err.println("[Config] db.properties introuvable — valeurs par défaut utilisées.");
            System.err.println("[Config] Copiez db.properties.example → db.properties et configurez vos identifiants.");
        }

        DB_URL  = dbUrl;
        DB_USER = dbUser;
        DB_PASS = dbPass;
    }
}
