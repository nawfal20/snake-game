#!/bin/bash
# ============================================================
# Script de push du projet Snake Game vers GitHub
# Exécutez ce script depuis la racine du projet extrait
# ============================================================

set -e  # Arrêter en cas d'erreur

REPO_URL="https://github.com/nawfal20/snake-game.git"
BRANCH="main"

echo "🐍 Initialisation du push Snake Game → GitHub"
echo "--------------------------------------------"

# Initialiser Git si pas déjà fait
if [ ! -d ".git" ]; then
  git init
  echo "✅ Dépôt Git initialisé"
fi

# Supprimer l'ancien remote s'il existe, puis le reconfigurer
git remote remove origin 2>/dev/null || true
git remote add origin "$REPO_URL"
echo "✅ Remote configuré : $REPO_URL"

# S'assurer qu'on est sur la branche main
git checkout -B "$BRANCH"

# Ajouter tous les fichiers (db.properties exclu par .gitignore)
git add .

# Vérifier que db.properties n'est pas tracké
if git diff --cached --name-only | grep -q "^db.properties$"; then
  echo "❌ ERREUR : db.properties est sur le point d'être committé !"
  echo "   Vérifiez votre .gitignore."
  exit 1
fi

echo "✅ Fichiers stagés (db.properties exclu)"

# Commit
git commit -m "🐍 Snake Game Pro — version finale

- Architecture MVC Java Swing
- Modes 1 et 2 joueurs
- Système de coins et store de skins
- Persistance MySQL (scores, joueurs, items)
- Identifiants BDD externalisés dans db.properties (non commité)
- Diagrammes UML fournis (classe + séquence)
- README et guide d'installation complets"

echo "✅ Commit créé"

# Push en forçant (remplace tout ce qui est sur GitHub)
echo ""
echo "📤 Push vers GitHub (force — écrase le contenu existant)..."
git push --force origin "$BRANCH"

echo ""
echo "🎉 Push terminé avec succès !"
echo "   Dépôt : $REPO_URL"
