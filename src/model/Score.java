package model;

import java.sql.Timestamp;

public class Score {
    private int id;
    private String playerName;
    private int score;
    private int duration;
    private int level;
    private int coinsCollected;
    private Timestamp createdAt;

    public Score(int id, String playerName, int score, int duration, int level, int coinsCollected, Timestamp createdAt) {
        this.id = id;
        this.playerName = playerName;
        this.score = score;
        this.duration = duration;
        this.level = level;
        this.coinsCollected = coinsCollected;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public String getPlayerName() { return playerName; }
    public int getScore() { return score; }
    public int getDuration() { return duration; }
    public int getLevel() { return level; }
    public int getCoinsCollected() { return coinsCollected; }
    public Timestamp getCreatedAt() { return createdAt; }
}
