package database;

import java.util.List;
import model.Player;
import model.Score;
import model.Store;

public interface ScoreDAO {
    void saveScore(String playerName, int score, int duration, int level, int coinsCollected);
    List<Score> getTopScores(int limit);
    Player getPlayer(String username);
    void savePlayer(Player player);
    void updatePlayerCoins(String username, int newCoins);
    
    // Store and Skins DB operations
    List<Store.StoreItem> getStoreItems();
    List<String> getOwnedSkins(int playerId);
    boolean buySkin(int playerId, int itemId, int price);
    void updateSelectedSkin(int playerId, String skinName);
    int getBestScore(String username);
    List<Player> getAllPlayers();
    void updateSkinPrice(int itemId, int newPrice);
}
