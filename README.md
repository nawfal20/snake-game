# Snake Game Pro 🐍

Un jeu du serpent complet développé en **Java (Swing)** avec architecture MVC, support multijoueur local, système de coins et magasin, et base de données MySQL pour la persistance.

## 🚀 Prérequis

- **Java Development Kit (JDK) 17** ou supérieur
- **MySQL Server** (XAMPP, WAMP, ou MySQL Installer)
- **VS Code** avec l'extension *Extension Pack for Java* (ou IntelliJ IDEA / Eclipse)

---

## 🛠️ Installation et Configuration

### 1. Cloner le projet

```bash
git clone https://github.com/nawfal20/snake-game.git
cd snake-game
```

### 2. Base de données MySQL

Démarrez votre serveur MySQL, puis importez le schéma :

```bash
mysql -u root -p < database.sql
```

Ou copiez-collez le contenu de `database.sql` dans phpMyAdmin.

### 3. Configurer les identifiants de base de données

> ⚠️ Les identifiants ne sont **jamais** stockés dans le code source.

Copiez le fichier d'exemple et renseignez vos valeurs :

```bash
cp db.properties.example db.properties
```

Éditez `db.properties` :

```properties
db.url=jdbc:mysql://localhost:3306/snakegame?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=root
db.pass=VotreMotDePasseIci
```

> `db.properties` est exclu de Git via `.gitignore` — il ne sera jamais publié.

### 4. Connecteur JDBC

Le fichier `lib/mysql-connector-j-8.4.0.jar` est inclus dans le dépôt.

**Sous VS Code :**
- Vue *Java Projects* → *Referenced Libraries* → `+` → sélectionnez `lib/mysql-connector-j-8.4.0.jar`

### 5. Lancer le jeu

```bash
# Via Makefile
make run

# Ou manuellement
javac -cp lib/mysql-connector-j-8.4.0.jar -d bin src/**/*.java src/*.java
java -cp bin:lib/mysql-connector-j-8.4.0.jar Main
```

Sous Windows, remplacez `:` par `;` dans le classpath.

---

## 🎮 Commandes

| Action | Joueur 1 | Joueur 2 |
|--------|----------|----------|
| Haut   | ↑        | Z / W    |
| Bas    | ↓        | S        |
| Gauche | ←        | Q / A    |
| Droite | →        | D        |
| Pause  | Échap    | —        |

---

## ✨ Fonctionnalités

- 🎮 Modes **1 Joueur** et **2 Joueurs** local
- 🍎 Nourriture positive, négative et pièces (coins)
- 🛒 **Store** : achetez des skins avec vos pièces
- 📊 Enregistrement et classement des scores en base de données
- ⚙️ Paramètres de vitesse configurables

---

## 📁 Structure du projet

```
snake-game/
├── src/
│   ├── Main.java
│   ├── model/          # Logique métier (Snake, Food, GameBoard…)
│   ├── view/           # Interface graphique Swing
│   ├── controller/     # Contrôleurs (GameController, KeyboardHandler)
│   ├── database/       # Accès BDD (DatabaseManager, ScoreDAO…)
│   └── utils/          # Constantes et utilitaires
├── lib/
│   └── mysql-connector-j-8.4.0.jar
├── database.sql            # Script d'initialisation MySQL
├── db.properties.example   # Modèle de configuration BDD (à copier)
├── db.properties           # ⚠️ IGNORÉ par Git — vos identifiants locaux
├── Makefile
└── README.md
```

---

## 👥 Équipe

| Membre | Rôle |
|--------|------|
| Ayoub Agchar | Développeur |
| Houssam Eddine Sminou | Développeur |
| Naoufal Essadki | Développeur |

*Projet Logiciel – AIAC GI22 – 2025/2026*
