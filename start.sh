#!/bin/bash

# Script de démarrage rapide pour l'application Gestion des Mémoires de Soutenance
# Auteur: Assistant IA
# Version: 1.0.0

set -e  # Arrêter en cas d'erreur

echo "🎓 Système de Gestion des Mémoires de Soutenance"
echo "=================================================="
echo ""

# Couleurs pour les messages
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Fonction pour afficher les messages colorés
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Vérification des prérequis
print_status "Vérification des prérequis..."

# Vérifier Java
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n1 | cut -d'"' -f2)
    print_success "Java trouvé: $JAVA_VERSION"
else
    print_error "Java n'est pas installé. Veuillez installer Java 17 ou supérieur."
    exit 1
fi

# Vérifier PostgreSQL
if command -v psql &> /dev/null; then
    print_success "PostgreSQL trouvé"
else
    print_warning "PostgreSQL n'est pas trouvé dans le PATH. Assurez-vous qu'il est installé et en cours d'exécution."
fi

# Choix du mode de démarrage
echo ""
echo "Choisissez le mode de démarrage:"
echo "1. Développement (avec base de données locale)"
echo "2. Docker (avec docker-compose)"
echo "3. Production (JAR pré-compilé)"
echo ""
read -p "Votre choix (1-3): " choice

case $choice in
    1)
        print_status "Démarrage en mode développement..."
        
        # Vérifier la base de données
        print_status "Vérification de la base de données PostgreSQL..."
        
        if ! pg_isready -h localhost -p 5432 -U postgres &> /dev/null; then
            print_warning "PostgreSQL ne semble pas être en cours d'exécution."
            print_status "Tentative de démarrage de PostgreSQL..."
            
            # Essayer différentes commandes selon l'OS
            if command -v systemctl &> /dev/null; then
                sudo systemctl start postgresql
            elif command -v service &> /dev/null; then
                sudo service postgresql start
            elif command -v brew &> /dev/null; then
                brew services start postgresql
            else
                print_error "Impossible de démarrer PostgreSQL automatiquement. Veuillez le démarrer manuellement."
                exit 1
            fi
        fi
        
        # Créer la base de données si elle n'existe pas
        print_status "Création de la base de données si nécessaire..."
        createdb -h localhost -U postgres memoires_db 2>/dev/null || print_warning "La base de données existe déjà ou erreur de création"
        
        # Compiler et démarrer l'application
        print_status "Compilation et démarrage de l'application..."
        ./gradlew bootRun
        ;;
    
    2)
        print_status "Démarrage avec Docker..."
        
        if ! command -v docker &> /dev/null; then
            print_error "Docker n'est pas installé. Veuillez installer Docker et Docker Compose."
            exit 1
        fi
        
        if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
            print_error "Docker Compose n'est pas installé."
            exit 1
        fi
        
        print_status "Construction et démarrage des conteneurs..."
        if command -v docker-compose &> /dev/null; then
            docker-compose up --build
        else
            docker compose up --build
        fi
        ;;
    
    3)
        print_status "Démarrage en mode production..."
        
        if [ ! -f "build/libs/memoires-soutenance-1.0.0-boot.jar" ]; then
            print_status "JAR non trouvé. Compilation en cours..."
            ./gradlew bootJar
        fi
        
        print_status "Démarrage de l'application..."
        java -jar build/libs/memoires-soutenance-1.0.0-boot.jar
        ;;
    
    *)
        print_error "Choix invalide. Veuillez choisir 1, 2 ou 3."
        exit 1
        ;;
esac

print_success "Application démarrée avec succès!"
echo ""
echo "🌐 Accédez à l'application: http://localhost:8080"
echo "👤 Utilisateur par défaut: admin / admin"
echo ""
echo "Pour arrêter l'application, appuyez sur Ctrl+C"