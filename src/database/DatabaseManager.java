package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import utils.Constants;


public class DatabaseManager {
    private static Connection connection = null;
    private static boolean connectionAttempted = false;

    private DatabaseManager() { }

    
    public static Connection getConnection() {
        try {
            // Re-check if connection is still alive
            if (connection != null && !connection.isClosed() && connection.isValid(2)) {
                return connection;
            }
        } catch (SQLException e) {
            // Connection broken, will attempt reconnect
            connection = null;
        }

        if (!connectionAttempted) {
            connectionAttempted = true;
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(
                    Constants.DB_URL, Constants.DB_USER, Constants.DB_PASS
                );
                System.out.println("Connexion à la base de données réussie.");
            } catch (ClassNotFoundException | SQLException e) {
                System.err.println("Echec de connexion BDD : " + e.getMessage());
                System.err.println("Vérifiez que MySQL est lancé et que la base 'snakegame' existe (database.sql).");
                System.err.println("Vérifiez aussi DB_USER et DB_PASS dans utils/Constants.java");
            }
        }
        return connection;
    }

    public static boolean isConnected() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(1);
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Force une nouvelle tentative de connexion (bouton "Retry" dans le menu).
     */
    public static void retryConnection() {
        connectionAttempted = false;
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ignored) { }
        connection = null;
        getConnection();
    }

    /**
     * À appeler uniquement à la fermeture de l'application.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                connection = null;
                connectionAttempted = false;
            }
        }
    }
}
