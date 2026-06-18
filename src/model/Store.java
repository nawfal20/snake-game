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

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public String getType() {
            return type;
        }
    }

    private List<StoreItem> items;

    public static int getDefaultPrice(int itemId) {
        switch (itemId) {
            case 1:
                return 100; // Neon Snake
            case 2:
                return 500; // Golden Snake
            case 3:
                return 1000; // Diamond Snake
            case 4:
                return 300; // Fire Snake
            case 5:
                return 300; // Ice Snake
            case 6:
                return 800; // Phantom Snake
            case 7:
                return 1500; // Rainbow Snake
            case 8:
                return 200; // Coin Magnet
            case 9:
                return 1000; // Invincibility (10s)
            case 10:
                return 50; // Speed Boost (10s)
            default:
                return 0;
        }
    }

    public Store() {
        List<StoreItem> defaultItems = new ArrayList<>();
        defaultItems.add(new StoreItem(1, "Neon Snake", 100, "SKIN"));
        defaultItems.add(new StoreItem(2, "Golden Snake", 500, "SKIN"));
        defaultItems.add(new StoreItem(3, "Diamond Snake", 1000, "SKIN"));
        defaultItems.add(new StoreItem(4, "Fire Snake", 300, "SKIN"));
        defaultItems.add(new StoreItem(5, "Ice Snake", 300, "SKIN"));
        defaultItems.add(new StoreItem(6, "Phantom Snake", 800, "SKIN"));
        defaultItems.add(new StoreItem(7, "Rainbow Snake", 1500, "SKIN"));
        defaultItems.add(new StoreItem(8, "Coin Magnet", 200, "EFFECT"));
        defaultItems.add(new StoreItem(9, "Invincibility (10s)", 1000, "EFFECT"));
        defaultItems.add(new StoreItem(10, "Speed Boost", 50, "EFFECT"));

        items = new ArrayList<>();
        try {
            ScoreDAO dao = new ScoreDAOImpl();
            items = dao.getStoreItems();

            boolean synced = false;
            for (StoreItem defItem : defaultItems) {
                boolean exists = false;
                boolean needsUpdate = false;
                for (StoreItem dbItem : items) {
                    if (dbItem.getId() == defItem.getId()) {
                        exists = true;
                        if (!dbItem.getName().equalsIgnoreCase(defItem.getName()) 
                                || !dbItem.getType().equalsIgnoreCase(defItem.getType())) {
                            needsUpdate = true;
                        }
                        break;
                    }
                }
                if (!exists || needsUpdate) {
                    dao.saveStoreItem(defItem);
                    synced = true;
                }
            }

            // Clean up obsolete items from DB
            for (StoreItem dbItem : items) {
                boolean exists = false;
                for (StoreItem defItem : defaultItems) {
                    if (defItem.getId() == dbItem.getId()) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    dao.deleteStoreItem(dbItem.getId());
                    synced = true;
                }
            }

            if (synced) {
                items = dao.getStoreItems();
            }
        } catch (Exception e) {
            System.err.println("Fallback to default store items due to DB issue.");
        }

        if (items == null || items.isEmpty()) {
            items = defaultItems;
        }
    }

    public List<StoreItem> getItems() {
        return items;
    }

    public void updateItemPrice(int itemId, int newPrice) {
        // Update in memory
        for (StoreItem item : items) {
            if (item.getId() == itemId) {
                item.setPrice(newPrice);
                break;
            }
        }
        // Update in database
        try {
            ScoreDAO dao = new ScoreDAOImpl();
            dao.updateSkinPrice(itemId, newPrice);
        } catch (Exception e) {
            System.err.println("Could not update price in database: " + e.getMessage());
        }
    }
}
