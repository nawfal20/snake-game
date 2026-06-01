package model;

import java.util.ArrayList;
import java.util.List;
import database.ScoreDAO;
import database.ScoreDAOImpl;

public class Store {
    
    public static class StoreItem {
        private int id;
        private String name;
        private int price;
        private String type; // SKIN, COLOR, EFFECT

        public StoreItem(int id, String name, int price, String type) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.type = type;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public int getPrice() { return price; }
        public String getType() { return type; }
    }

    private List<StoreItem> items;

    public Store() {
        items = new ArrayList<>();
        try {
            ScoreDAO dao = new ScoreDAOImpl();
            items = dao.getStoreItems();
        } catch (Exception e) {
            System.err.println("Fallback to default store items due to DB issue.");
        }
        
        if (items == null || items.isEmpty()) {
            items = new ArrayList<>();
            // Fallback default items
            items.add(new StoreItem(1, "Neon Snake", 100, "SKIN"));
            items.add(new StoreItem(2, "Golden Snake", 500, "SKIN"));
            items.add(new StoreItem(3, "Diamond Snake", 1000, "SKIN"));
            items.add(new StoreItem(4, "Fire Snake", 300, "SKIN"));
            items.add(new StoreItem(5, "Ice Snake", 300, "SKIN"));
            items.add(new StoreItem(6, "Phantom Snake", 800, "SKIN"));
            items.add(new StoreItem(7, "Rainbow Snake", 1500, "SKIN"));
            items.add(new StoreItem(8, "Speed Boost", 50, "EFFECT"));
            items.add(new StoreItem(9, "Coin Magnet", 200, "EFFECT"));
            items.add(new StoreItem(10, "Invincibility (10s)", 1000, "EFFECT"));
        }
    }

    public List<StoreItem> getItems() {
        return items;
    }
}
