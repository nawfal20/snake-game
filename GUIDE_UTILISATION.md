# Guide d'utilisation - Snake Game Pro 🐍

Bienvenue dans **Snake Game Pro**, un jeu classique du serpent amélioré et développé en Java (Swing) avec :
- Une architecture MVC.
- Un support de base de données **MySQL** pour la gestion des profils et scores.
- Un magasin de **Skins customisés** achetables avec des pièces récoltées en jeu.
- Un système de **vitesse progressive par niveau**.

---

## 🛠️ Configuration de la Base de Données

Ce jeu nécessite un serveur de base de données MySQL (par exemple via XAMPP, WAMP, ou MySQL Installer).

1. Démarrez vos services **Apache** (optionnel) et **MySQL**.
2. Connectez-vous à **phpMyAdmin** (ex: `http://localhost/phpmyadmin`) ou à votre console MySQL.
3. Importez ou exécutez le script SQL contenu dans le fichier `database.sql` :
   - Le script crée une base de données nommée `snakegame`.
   - Il crée les tables nécessaires (`players`, `scores`, `store_items`, `player_items`).
   - Il insère automatiquement les skins et articles disponibles dans la boutique.

*Note : Les paramètres de connexion par défaut sont définis dans `src/utils/Constants.java` :*
- **URL** : `jdbc:mysql://localhost:3306/snakegame`
- **Utilisateur** : `root`
- **Mot de passe** : *(vide)*

Si votre serveur MySQL utilise un mot de passe, un port ou un utilisateur différent, modifiez ces valeurs dans [Constants.java](file:///c:/Users/nawfa/Downloads/snake/snake/src/utils/Constants.java) aux lignes 42 à 44 avant de compiler le jeu.

### 🟢 Indicateur de connexion en temps réel
Le menu principal affiche désormais le statut de la base de données :
- **Point vert (`● Database connected`)** : Tout est connecté et fonctionnel. Vos scores, pièces et skins achetés seront enregistrés.
- **Panneau d'avertissement jaune (`⚠️ Database Offline`)** : La base de données n'est pas joignable (ex: MySQL éteint ou mal configuré). Le jeu bascule automatiquement en mode "invité local" pour ne pas bloquer le jeu. Aucun score ou pièce ne sera persisté.
- **Astuce de reconnexion** : Si vous lancez le jeu puis démarrez votre serveur MySQL plus tard, vous n'avez pas besoin de relancer le jeu ! Cliquez simplement sur **"Change Profile"** dans le menu principal et validez pour forcer une tentative de reconnexion.

---

## 🚀 Compilation et Exécution du Jeu

Le projet intègre des scripts automatisés pour simplifier l'exécution sous Windows ou d'autres plateformes.

### Option 1 : Scripts Batch Windows (Recommandé)
- **`run.bat`** : Compile tous les fichiers sources Java en intégrant le connecteur MySQL et lance directement le jeu. Double-cliquez simplement dessus.
- **`build.bat`** : Compile les fichiers et génère une archive exécutable **`SnakeGame.jar`** dans le dossier racine. Double-cliquez dessus pour créer le JAR, que vous pourrez ensuite lancer directement en double-cliquant dessus.

### Option 2 : Via Makefile (Pour les utilisateurs de `make`)
Ouvrez votre terminal dans le dossier du projet et exécutez :
- **`make compile`** : Compile les fichiers dans le dossier `bin/`.
- **`make run`** : Lance le jeu.
- **`make jar`** : Génère le fichier exécutable `SnakeGame.jar`.
- **`make clean`** : Nettoie les fichiers de compilation.

---

## 🎮 Gameplay et Fonctionnalités

### 1. Gestion des Profils Utilisateurs (MySQL)
- Au lancement du jeu ou en cliquant sur **"Change Profile"** dans le menu principal, vous pouvez renseigner votre pseudo.
- Si le profil existe dans MySQL, vos pièces accumulées et vos skins achetés seront chargés automatiquement.
- Si le profil n'existe pas, un nouveau profil est créé dans la base de données avec `0` pièce et le skin `default` équipé.
- Le menu affiche en temps réel votre solde de pièces et votre **meilleur score personnel** (`Best: X`) enregistré en base de données.

### 2. Le Magasin de Skins (`Store / Shop`)
Le magasin affiche les différents skins pour votre serpent. Vous pouvez les acheter avec les pièces collectées en jeu :
- **Default** : Le skin vert néon de départ (gratuit).
- **Neon Snake** (100 pièces) : Tête cyan lumineuse et corps rose néon.
- **Golden Snake** (500 pièces) : Serpent doré brillant couronné.
- **Diamond Snake** (1000 pièces) : Segments dessinés en forme de losanges bleus glacés.
- **Fire Snake** (300 pièces) : Serpent animé affichant des dégradés rouge/orange (effet feu).
- **Ice Snake** (300 pièces) : Blocs de glace rectangulaires bleus et blancs.
- **Phantom Snake** (800 pièces) : Serpent semi-transparent violet-bleu.
- **Rainbow Snake** (1500 pièces) : Serpent arc-en-ciel dont les couleurs se déplacent de manière fluide.

*Les achats et équipements de skins sont immédiatement enregistrés et persistés dans les tables SQL `player_items` et `players`.*

### 3. Vitesse progressive par Niveau
- Pendant la partie, manger de la nourriture normale augmente votre score.
- **Tous les 5 points**, vous gagnez un **niveau supérieur** (Level Up).
- Chaque passage de niveau augmente la vitesse du serpent (le délai de rafraîchissement diminue de `12ms` par niveau).
- Un signal visuel clignotant **"LEVEL UP! Speed Increased!"** s'affiche au centre de l'écran lors du changement de niveau.

### 4. Historique détaillé des parties (`Scores`)
- La page **Scores** affiche désormais un tableau de bord complet avec l'historique complet des meilleures parties.
- Vous y trouverez des informations détaillées ("traces") de chaque partie :
  - Le nom du joueur.
  - Le score atteint.
  - Le niveau maximum atteint.
  - Le nombre de pièces récoltées au cours de la partie.
  - La durée de la partie en secondes.
  - La date et l'heure de la partie.

---

## ⌨️ Commandes du Jeu

- **Joueur 1** : Touches Fléchées (Haut, Bas, Gauche, Droite)
- **Joueur 2 (Local)** : Touches ZQSD / WASD
- **Mettre en pause** : Touche **Échap (ESC)**
- **Retourner au menu principal** : Touche **Échap (ESC)** sur l'écran Game Over
