# Système de Gestion des Mémoires de Soutenance

Une application web moderne pour la gestion des mémoires de soutenance dans un établissement académique, développée avec Spring Boot, Kotlin, PostgreSQL et Bootstrap.

## 🎯 Fonctionnalités

### Gestion des Étudiants
- ✅ Inscription et gestion des profils étudiants
- ✅ Recherche et filtrage par nom, filière, niveau
- ✅ Suivi des mémoires par étudiant

### Gestion des Professeurs
- ✅ Gestion des profils professeurs
- ✅ Organisation par département et spécialité
- ✅ Suivi des encadrements et participations aux jurys

### Gestion des Mémoires
- ✅ Création et suivi des mémoires
- ✅ Affectation des directeurs et co-directeurs
- ✅ Gestion des statuts (En cours, Déposé, Validé, Soutenu)
- ✅ Recherche par titre, mots-clés, type

### Gestion des Soutenances
- ✅ Planification des soutenances
- ✅ Gestion des salles et créneaux
- ✅ Attribution des jurys
- ✅ Saisie des notes et mentions

### Gestion des Jurys
- ✅ Constitution des jurys avec président et membres
- ✅ Validation de la composition
- ✅ Suivi des participations

### Interface Utilisateur
- ✅ Interface moderne avec Bootstrap 5
- ✅ Design responsive pour mobile et desktop
- ✅ Tableau de bord avec statistiques
- ✅ Recherche et filtrage avancés

## 🛠️ Technologies Utilisées

- **Backend**: Spring Boot 3.2.0, Kotlin 1.9.20
- **Base de données**: PostgreSQL
- **Frontend**: Thymeleaf, Bootstrap 5, JavaScript
- **Sécurité**: Spring Security
- **Build**: Gradle avec Kotlin DSL
- **ORM**: JPA/Hibernate

## 📋 Prérequis

- Java 17 ou supérieur
- PostgreSQL 12 ou supérieur
- Gradle 7.0 ou supérieur (ou utiliser le wrapper inclus)

## 🚀 Installation et Démarrage

### 1. Cloner le projet
```bash
git clone <url-du-projet>
cd memoires-soutenance
```

### 2. Configurer la base de données
Créer une base de données PostgreSQL :
```sql
CREATE DATABASE memoires_soutenance;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE memoires_soutenance TO postgres;
```

### 3. Configuration
Modifier le fichier `src/main/resources/application.yml` si nécessaire :
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/memoires_soutenance
    username: postgres
    password: postgres
```

### 4. Lancer l'application
```bash
# Avec Gradle wrapper (recommandé)
./gradlew bootRun

# Ou avec Gradle installé
gradle bootRun
```

L'application sera accessible sur : http://localhost:8080

## 👤 Comptes de Démonstration

L'application inclut des comptes de test :

| Utilisateur | Mot de passe | Rôle |
|-------------|--------------|------|
| admin | admin | Administrateur |
| professeur | professeur | Professeur |
| etudiant | etudiant | Étudiant |
| user | user | Utilisateur |

## 📊 Structure du Projet

```
src/
├── main/
│   ├── kotlin/com/academique/memoires/
│   │   ├── config/          # Configuration Spring
│   │   ├── controller/      # Contrôleurs MVC
│   │   ├── entity/          # Entités JPA
│   │   ├── repository/      # Repositories
│   │   ├── service/         # Services métier
│   │   └── MemoiresSoutenanceApplication.kt
│   └── resources/
│       ├── static/          # CSS, JS, images
│       ├── templates/       # Templates Thymeleaf
│       └── application.yml  # Configuration
└── test/                    # Tests unitaires
```

## 🗂️ Modèle de Données

### Entités Principales

- **Etudiant** : Informations des étudiants
- **Professeur** : Informations des professeurs
- **Memoire** : Mémoires avec directeur/co-directeur
- **Soutenance** : Planification des soutenances
- **Jury** : Composition des jurys

### Relations
- Un étudiant peut avoir plusieurs mémoires
- Un mémoire a un directeur et optionnellement un co-directeur
- Une soutenance est liée à un mémoire et un jury
- Un jury a un président et plusieurs membres

## 🔧 Configuration Avancée

### Base de données
Pour utiliser une autre base de données, modifier les dépendances dans `build.gradle.kts` et la configuration dans `application.yml`.

### Sécurité
La configuration de sécurité se trouve dans `SecurityConfig.kt`. En production, il est recommandé de :
- Activer CSRF
- Utiliser une base de données pour les utilisateurs
- Configurer HTTPS

### Personnalisation
- **CSS** : Modifier `src/main/resources/static/css/custom.css`
- **JavaScript** : Modifier `src/main/resources/static/js/custom.js`
- **Templates** : Les templates Thymeleaf sont dans `src/main/resources/templates/`

## 🧪 Tests

Lancer les tests :
```bash
./gradlew test
```

## 📦 Build et Déploiement

### Créer un JAR exécutable
```bash
./gradlew bootJar
```

Le JAR sera généré dans `build/libs/`

### Déploiement
```bash
java -jar build/libs/memoires-soutenance-0.0.1-SNAPSHOT.jar
```

## 🤝 Contribution

1. Fork le projet
2. Créer une branche feature (`git checkout -b feature/nouvelle-fonctionnalite`)
3. Commit les changements (`git commit -am 'Ajout nouvelle fonctionnalité'`)
4. Push sur la branche (`git push origin feature/nouvelle-fonctionnalite`)
5. Créer une Pull Request

## 📝 TODO / Améliorations Futures

- [ ] API REST pour intégration mobile
- [ ] Notifications par email
- [ ] Génération de rapports PDF
- [ ] Import/Export Excel
- [ ] Calendrier interactif
- [ ] Système de workflow avancé
- [ ] Authentification LDAP/OAuth2
- [ ] Audit trail des modifications
- [ ] Tableau de bord analytics
- [ ] Support multi-langue

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

## 📞 Support

Pour toute question ou problème :
- Créer une issue sur GitHub
- Contacter l'équipe de développement

---

**Développé avec ❤️ pour l'éducation académique**