# Système de Gestion des Mémoires de Soutenance

## Description

Application web complète développée avec **Kotlin Multiplatform**, **Spring Boot**, **PostgreSQL**, **Thymeleaf** et **Bootstrap** pour la gestion des mémoires de soutenance dans un établissement académique.

## Technologies Utilisées

### Backend
- **Kotlin Multiplatform** 1.9.20 - Langage de programmation moderne
- **Spring Boot** 3.2.0 - Framework pour applications web
- **Spring Data JPA** - ORM et persistance des données
- **Spring Security** - Sécurisation de l'application
- **PostgreSQL** - Base de données relationnelle

### Frontend
- **Thymeleaf** - Moteur de templates côté serveur
- **Bootstrap** 5.3.2 - Framework CSS responsive
- **Bootstrap Icons** - Icônes vectorielles
- **Chart.js** - Graphiques et visualisations

### Outils de Build
- **Gradle** avec Kotlin DSL - Système de build
- **Docker** (optionnel) - Conteneurisation

## Fonctionnalités

### Gestion des Étudiants
- ✅ Création, modification, suppression d'étudiants
- ✅ Recherche avancée par nom, prénom, email, filière
- ✅ Gestion des niveaux d'étude (Licence, Master, Doctorat)
- ✅ Validation des données avec contraintes d'unicité

### Gestion des Enseignants
- ✅ Gestion complète des enseignants
- ✅ Grades hiérarchiques (Professeur, Maître de conférences, etc.)
- ✅ Spécialités et départements
- ✅ Disponibilités pour les jurys

### Gestion des Mémoires
- ✅ Cycle complet : création → dépôt → validation → soutenance
- ✅ Upload de fichiers PDF
- ✅ Statuts multiples (En cours, Déposé, Validé, Soutenu, etc.)
- ✅ Types de mémoires (Licence, Master, Thèse)
- ✅ Système de notation et mentions

### Gestion des Jurys
- ✅ Planification des soutenances
- ✅ Composition des jurys avec validation des contraintes
- ✅ Gestion des conflits d'horaires
- ✅ Procès-verbaux et observations

### Tableau de Bord
- ✅ Statistiques en temps réel
- ✅ Graphiques interactifs (Chart.js)
- ✅ Prochaines soutenances
- ✅ Mémoires en attente de planification
- ✅ Actions rapides

## Structure du Projet

```
memoires-soutenance/
├── build.gradle.kts              # Configuration Gradle principale
├── gradle.properties             # Propriétés Gradle
├── settings.gradle.kts           # Configuration des modules
├── README.md                     # Documentation
├── docker-compose.yml            # Configuration Docker
├── src/
│   ├── commonMain/kotlin/        # Code commun Kotlin Multiplatform
│   │   └── com/memoires/common/
│   │       └── model/            # Modèles partagés
│   └── jvmMain/
│       ├── kotlin/com/memoires/server/
│       │   ├── MemoiresSoutenanceApplication.kt  # Application principale
│       │   ├── entity/           # Entités JPA
│       │   │   ├── Etudiant.kt
│       │   │   ├── Enseignant.kt
│       │   │   ├── Memoire.kt
│       │   │   └── Jury.kt
│       │   ├── repository/       # Repositories Spring Data
│       │   │   ├── EtudiantRepository.kt
│       │   │   ├── EnseignantRepository.kt
│       │   │   ├── MemoireRepository.kt
│       │   │   └── JuryRepository.kt
│       │   ├── service/          # Services métier
│       │   │   ├── EtudiantService.kt
│       │   │   ├── MemoireService.kt
│       │   │   └── JuryService.kt
│       │   ├── controller/       # Contrôleurs web
│       │   │   └── HomeController.kt
│       │   ├── config/           # Configuration Spring
│       │   └── security/         # Configuration sécurité
│       └── resources/
│           ├── application.yml   # Configuration application
│           ├── templates/        # Templates Thymeleaf
│           │   ├── layout.html   # Template de base
│           │   └── index.html    # Page d'accueil
│           ├── static/           # Ressources statiques
│           │   ├── css/
│           │   ├── js/
│           │   └── images/
│           └── uploads/          # Fichiers uploadés
```

## Installation et Configuration

### Prérequis
- **Java 17+**
- **PostgreSQL 12+**
- **Gradle 8+** (ou utiliser le wrapper inclus)

### 1. Cloner le projet
```bash
git clone <repository-url>
cd memoires-soutenance
```

### 2. Configuration de la base de données

#### Créer la base de données PostgreSQL
```sql
CREATE DATABASE memoires_db;
CREATE USER memoires_user WITH PASSWORD 'memoires_password';
GRANT ALL PRIVILEGES ON DATABASE memoires_db TO memoires_user;
```

#### Modifier la configuration (si nécessaire)
Éditer `src/jvmMain/resources/application.yml` :
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/memoires_db
    username: memoires_user
    password: memoires_password
```

### 3. Compilation et exécution

#### Avec Gradle Wrapper (recommandé)
```bash
# Compilation
./gradlew build

# Exécution
./gradlew bootRun
```

#### Avec Gradle installé
```bash
# Compilation
gradle build

# Exécution
gradle bootRun
```

### 4. Accès à l'application
- **URL** : http://localhost:8080
- **Utilisateur par défaut** : admin / admin

## Configuration Docker (Optionnel)

### docker-compose.yml
```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: memoires_db
      POSTGRES_USER: memoires_user
      POSTGRES_PASSWORD: memoires_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - postgres
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/memoires_db

volumes:
  postgres_data:
```

### Démarrage avec Docker
```bash
docker-compose up -d
```

## Explication du Code

### Architecture

L'application suit le pattern **MVC (Model-View-Controller)** avec une architecture en couches :

1. **Couche Présentation** : Contrôleurs Spring MVC + Templates Thymeleaf
2. **Couche Service** : Logique métier et validation
3. **Couche Repository** : Accès aux données avec Spring Data JPA
4. **Couche Entité** : Modèles de données JPA

### Entités JPA

#### Etudiant.kt
```kotlin
@Entity
@Table(name = "etudiants")
data class Etudiant(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(unique = true, nullable = false)
    val numeroEtudiant: String,
    
    @Column(nullable = false)
    val nom: String,
    
    // ... autres propriétés
    
    @OneToMany(mappedBy = "etudiant", cascade = [CascadeType.ALL])
    val memoires: MutableList<Memoire> = mutableListOf()
)
```

**Explications** :
- `@Entity` : Marque la classe comme entité JPA
- `@Table` : Spécifie le nom de la table et contraintes
- `@Id @GeneratedValue` : Clé primaire auto-générée
- `@Column` : Configuration des colonnes (contraintes, taille)
- `@OneToMany` : Relation un-à-plusieurs avec Memoire

#### Relationships JPA

```kotlin
// Dans Memoire.kt
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "etudiant_id", nullable = false)
val etudiant: Etudiant

// Dans Jury.kt
@ManyToMany(fetch = FetchType.LAZY)
@JoinTable(
    name = "jury_membres",
    joinColumns = [JoinColumn(name = "jury_id")],
    inverseJoinColumns = [JoinColumn(name = "enseignant_id")]
)
val membres: MutableList<Enseignant> = mutableListOf()
```

### Repositories Spring Data

```kotlin
@Repository
interface EtudiantRepository : JpaRepository<Etudiant, Long> {
    
    // Méthode générée automatiquement
    fun findByNumeroEtudiant(numeroEtudiant: String): Optional<Etudiant>
    
    // Requête JPQL personnalisée
    @Query("""
        SELECT e FROM Etudiant e 
        WHERE LOWER(e.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
    """)
    fun searchEtudiants(@Param("searchTerm") searchTerm: String, pageable: Pageable): Page<Etudiant>
}
```

**Explications** :
- `JpaRepository<Etudiant, Long>` : Interface de base avec CRUD
- Spring génère les implémentations automatiquement
- `@Query` : Requêtes JPQL personnalisées
- `Pageable` : Support de la pagination

### Services Métier

```kotlin
@Service
@Transactional
class EtudiantService(
    private val etudiantRepository: EtudiantRepository
) {
    
    fun save(etudiant: Etudiant): Etudiant {
        // Validation métier
        if (etudiant.id == null && etudiantRepository.existsByNumeroEtudiant(etudiant.numeroEtudiant)) {
            throw IllegalArgumentException("Le numéro étudiant existe déjà")
        }
        
        return etudiantRepository.save(etudiant)
    }
}
```

**Explications** :
- `@Service` : Composant Spring de logique métier
- `@Transactional` : Gestion automatique des transactions
- Injection de dépendance du repository
- Validation métier avant sauvegarde

### Contrôleurs Web

```kotlin
@Controller
class HomeController(
    private val etudiantService: EtudiantService,
    private val memoireService: MemoireService
) {
    
    @GetMapping("/")
    fun home(model: Model): String {
        // Récupération des données
        val totalEtudiants = etudiantService.count()
        
        // Ajout au modèle pour la vue
        model.addAttribute("totalEtudiants", totalEtudiants)
        
        // Retour du nom du template
        return "index"
    }
}
```

**Explications** :
- `@Controller` : Contrôleur Spring MVC
- `@GetMapping` : Mapping des requêtes GET
- `Model` : Objet pour passer des données à la vue
- Retourne le nom du template Thymeleaf

### Templates Thymeleaf

```html
<!-- layout.html -->
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title th:text="${title + ' - Gestion Mémoires'}">Titre</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <nav class="navbar navbar-dark bg-dark">
        <a class="navbar-brand" th:href="@{/}">Gestion Mémoires</a>
    </nav>
    
    <main>
        <div th:block layout:fragment="content">
            <!-- Contenu spécifique -->
        </div>
    </main>
</body>
</html>
```

**Explications** :
- `xmlns:th` : Namespace Thymeleaf
- `th:text` : Insertion de texte dynamique
- `th:href="@{/}"` : URLs relatives
- `layout:fragment` : Système de templates

### Configuration Spring

```yaml
# application.yml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/memoires_db
    username: postgres
    password: postgres
    
  jpa:
    hibernate:
      ddl-auto: update  # Création/mise à jour automatique du schéma
    show-sql: true      # Affichage des requêtes SQL
    
  thymeleaf:
    cache: false        # Désactivation du cache en développement
```

## API REST (Future Extension)

L'application peut être étendue avec des endpoints REST :

```kotlin
@RestController
@RequestMapping("/api/etudiants")
class EtudiantRestController(
    private val etudiantService: EtudiantService
) {
    
    @GetMapping
    fun getAllEtudiants(pageable: Pageable): Page<Etudiant> {
        return etudiantService.findAll(pageable)
    }
    
    @PostMapping
    fun createEtudiant(@Valid @RequestBody etudiant: Etudiant): Etudiant {
        return etudiantService.save(etudiant)
    }
}
```

## Tests

### Tests d'Intégration
```kotlin
@SpringBootTest
@TestMethodOrder(OrderAnnotation::class)
class EtudiantServiceIntegrationTest {
    
    @Autowired
    private lateinit var etudiantService: EtudiantService
    
    @Test
    @Order(1)
    fun `should create etudiant`() {
        val etudiant = Etudiant(
            numeroEtudiant = "E001",
            nom = "Dupont",
            prenom = "Jean",
            email = "jean.dupont@example.com",
            niveauEtude = NiveauEtude.MASTER,
            filiere = "Informatique"
        )
        
        val saved = etudiantService.save(etudiant)
        assertThat(saved.id).isNotNull()
    }
}
```

## Sécurité

### Configuration Spring Security
```kotlin
@Configuration
@EnableWebSecurity
class SecurityConfig {
    
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers("/", "/about", "/contact").permitAll()
                    .anyRequest().authenticated()
            }
            .formLogin { form ->
                form.loginPage("/login").permitAll()
            }
            .build()
    }
}
```

## Déploiement

### Packaging JAR
```bash
./gradlew bootJar
java -jar build/libs/memoires-soutenance-1.0.0-boot.jar
```

### Variables d'Environnement
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://prod-db:5432/memoires_db
export SPRING_DATASOURCE_USERNAME=prod_user
export SPRING_DATASOURCE_PASSWORD=secure_password
```

## Contribution

1. Fork le projet
2. Créer une branche feature (`git checkout -b feature/nouvelle-fonctionnalite`)
3. Commit les changements (`git commit -am 'Ajout nouvelle fonctionnalité'`)
4. Push vers la branche (`git push origin feature/nouvelle-fonctionnalite`)
5. Créer une Pull Request

## Licence

Ce projet est sous licence MIT. Voir le fichier [LICENSE](LICENSE) pour plus de détails.

## Support

Pour toute question ou problème :
- **Email** : support@memoires.com
- **Issues GitHub** : [Créer une issue](https://github.com/votre-repo/issues)

## Roadmap

### Version 1.1
- [ ] API REST complète
- [ ] Authentication JWT
- [ ] Notifications email
- [ ] Export PDF des rapports

### Version 1.2
- [ ] Interface mobile responsive
- [ ] Intégration calendrier
- [ ] Workflow d'approbation
- [ ] Archivage automatique

---

**Développé avec ❤️ en Kotlin et Spring Boot**