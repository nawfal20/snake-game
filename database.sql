CREATE DATABASE IF NOT EXISTS snakegame;
USE snakegame;

CREATE TABLE IF NOT EXISTS players (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    total_coins INT DEFAULT 0,
    selected_skin VARCHAR(50) DEFAULT 'default'
);

CREATE TABLE IF NOT EXISTS scores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    player_name VARCHAR(50) NOT NULL,
    score INT NOT NULL,
    duration INT NOT NULL,
    level INT DEFAULT 1,
    coins_collected INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS store_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    item_name VARCHAR(50) NOT NULL,
    price INT NOT NULL,
    type VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS player_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    player_id INT NOT NULL,
    item_id INT NOT NULL,
    FOREIGN KEY (player_id) REFERENCES players(id),
    FOREIGN KEY (item_id) REFERENCES store_items(id)
);

-- Insert default store items matching model.Store
INSERT IGNORE INTO store_items (id, item_name, price, type) VALUES 
(1, 'Neon Snake', 100, 'SKIN'),
(2, 'Golden Snake', 500, 'SKIN'),
(3, 'Diamond Snake', 1000, 'SKIN'),
(4, 'Fire Snake', 300, 'SKIN'),
(5, 'Ice Snake', 300, 'SKIN'),
(6, 'Phantom Snake', 800, 'SKIN'),
(7, 'Rainbow Snake', 1500, 'SKIN'),
(8, 'Coin Magnet', 200, 'EFFECT'),
(9, 'Invincibility (10s)', 1000, 'EFFECT'),
(10, 'Speed Boost (10s)', 50, 'EFFECT');

-- Index pour accélérer les requêtes fréquentes
-- Index pour accélérer les requêtes fréquentes
CREATE INDEX idx_scores_player ON scores(player_name);
CREATE INDEX idx_scores_score ON scores(score DESC);
CREATE INDEX idx_player_items ON player_items(player_id, item_id);