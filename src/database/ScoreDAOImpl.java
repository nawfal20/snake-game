package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Player;
import model.Score;
import model.Store;


public class ScoreDAOImpl implements ScoreDAO {

    @Override
    public void saveScore(String playerName, int score, int duration, int level, int coinsCollected) {
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) return;
        String query = "INSERT INTO scores (player_name, score, duration, level, coins_collected) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, playerName);
            stmt.setInt(2, score);
            stmt.setInt(3, duration);
            stmt.setInt(4, level);
            stmt.setInt(5, coinsCollected);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur saveScore: " + e.getMessage());
        }
    }

    @Override
    public List<Score> getTopScores(int limit) {
        List<Score> scores = new ArrayList<>();
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) return scores;
        String query = "SELECT player_name, MAX(score) as max_score, MAX(level) as max_level, MAX(duration) as max_duration, SUM(coins_collected) as total_coins_collected, MAX(created_at) as recent_date FROM scores GROUP BY player_name ORDER BY max_score DESC LIMIT ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    scores.add(new Score(
                        0, // ID not needed for grouped score
                        rs.getString("player_name"),
                        rs.getInt("max_score"),
                        rs.getInt("max_duration"),
                        rs.getInt("max_level"),
                        rs.getInt("total_coins_collected"),
                        rs.getTimestamp("recent_date")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur getTopScores: " + e.getMessage());
        }
        return scores;
    }

    @Override
    public Player getPlayer(String username) {
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) return null;
        String query = "SELECT * FROM players WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Player(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getInt("total_coins"),
                        rs.getString("selected_skin")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur getPlayer: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void savePlayer(Player player) {
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) return;
        String query = "INSERT INTO players (username, total_coins, selected_skin) VALUES (?, ?, ?) " +
                       "ON DUPLICATE KEY UPDATE total_coins = ?, selected_skin = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, player.getUsername());
            stmt.setInt(2, player.getTotalCoins());
            stmt.setString(3, player.getSelectedSkin());
            stmt.setInt(4, player.getTotalCoins());
            stmt.setString(5, player.getSelectedSkin());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur savePlayer: " + e.getMessage());
        }
    }

    @Override
    public void updatePlayerCoins(String username, int newCoins) {
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) return;
        String query = "UPDATE players SET total_coins = ? WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, newCoins);
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur updatePlayerCoins: " + e.getMessage());
        }
    }

    @Override
    public List<Store.StoreItem> getStoreItems() {
        List<Store.StoreItem> items = new ArrayList<>();
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) return items;
        String query = "SELECT * FROM store_items ORDER BY type DESC, price ASC";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(new Store.StoreItem(
                        rs.getInt("id"),
                        rs.getString("item_name"),
                        rs.getInt("price"),
                        rs.getString("type")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur getStoreItems: " + e.getMessage());
        }
        return items;
    }

    @Override
    public List<String> getOwnedSkins(int playerId) {
        List<String> ownedSkins = new ArrayList<>();
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) return ownedSkins;
        String query = "SELECT si.item_name FROM player_items pi " +
                       "JOIN store_items si ON pi.item_id = si.id " +
                       "WHERE pi.player_id = ? AND si.type = 'SKIN'";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, playerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ownedSkins.add(rs.getString("item_name"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur getOwnedSkins: " + e.getMessage());
        }
        return ownedSkins;
    }

    @Override
    public boolean buySkin(int playerId, int itemId, int price) {
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) return false;
        try {
            conn.setAutoCommit(false);

            // Vérifier le solde
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT total_coins FROM players WHERE id = ?")) {
                stmt.setInt(1, playerId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next() || rs.getInt("total_coins") < price) {
                        conn.rollback();
                        conn.setAutoCommit(true);
                        return false;
                    }
                }
            }

            // Vérifier que l'item n'est pas déjà acheté
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT id FROM player_items WHERE player_id = ? AND item_id = ?")) {
                stmt.setInt(1, playerId);
                stmt.setInt(2, itemId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        conn.rollback();
                        conn.setAutoCommit(true);
                        return false; // Déjà possédé
                    }
                }
            }

            // Déduire les coins
            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE players SET total_coins = total_coins - ? WHERE id = ?")) {
                stmt.setInt(1, price);
                stmt.setInt(2, playerId);
                stmt.executeUpdate();
            }

            // Enregistrer l'achat
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO player_items (player_id, item_id) VALUES (?, ?)")) {
                stmt.setInt(1, playerId);
                stmt.setInt(2, itemId);
                stmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            System.err.println("Erreur buySkin: " + e.getMessage());
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @Override
    public void updateSelectedSkin(int playerId, String skinName) {
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) return;
        String query = "UPDATE players SET selected_skin = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, skinName);
            stmt.setInt(2, playerId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur updateSelectedSkin: " + e.getMessage());
        }
    }

    @Override
    public int getBestScore(String username) {
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) return 0;
        String query = "SELECT MAX(score) AS best_score FROM scores WHERE player_name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("best_score");
            }
        } catch (SQLException e) {
            System.err.println("Erreur getBestScore: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public List<Player> getAllPlayers() {
        List<Player> players = new ArrayList<>();
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) return players;
        String query = "SELECT * FROM players ORDER BY username ASC";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    players.add(new Player(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getInt("total_coins"),
                        rs.getString("selected_skin")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur getAllPlayers: " + e.getMessage());
        }
        return players;
    }

    @Override
    public void updateSkinPrice(int itemId, int newPrice) {
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) return;
        String query = "UPDATE store_items SET price = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, newPrice);
            stmt.setInt(2, itemId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur updateSkinPrice: " + e.getMessage());
        }
    }

    @Override
    public void saveStoreItem(Store.StoreItem item) {
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) return;
        String query = "INSERT INTO store_items (id, item_name, price, type) VALUES (?, ?, ?, ?) " +
                       "ON DUPLICATE KEY UPDATE item_name = ?, price = ?, type = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, item.getId());
            stmt.setString(2, item.getName());
            stmt.setInt(3, item.getPrice());
            stmt.setString(4, item.getType());
            stmt.setString(5, item.getName());
            stmt.setInt(6, item.getPrice());
            stmt.setString(7, item.getType());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur saveStoreItem: " + e.getMessage());
        }
    }

    @Override
    public void deleteStoreItem(int itemId) {
        Connection conn = DatabaseManager.getConnection();
        if (conn == null) return;
        String query = "DELETE FROM store_items WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, itemId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur deleteStoreItem: " + e.getMessage());
        }
    }
}
