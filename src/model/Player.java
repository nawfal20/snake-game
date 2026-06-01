package model;

public class Player {
    private int id;
    private String username;
    private int totalCoins;
    private String selectedSkin;

    public Player(int id, String username, int totalCoins, String selectedSkin) {
        this.id = id;
        this.username = username;
        this.totalCoins = totalCoins;
        this.selectedSkin = selectedSkin;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public int getTotalCoins() { return totalCoins; }
    public void setTotalCoins(int totalCoins) { this.totalCoins = totalCoins; }
    public String getSelectedSkin() { return selectedSkin; }
    public void setSelectedSkin(String selectedSkin) { this.selectedSkin = selectedSkin; }
}
