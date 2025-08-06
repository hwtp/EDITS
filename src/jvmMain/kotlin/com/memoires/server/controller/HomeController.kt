package com.memoires.server.controller

import com.memoires.server.entity.StatutMemoire
import com.memoires.server.entity.NiveauEtude
import com.memoires.server.service.EtudiantService
import com.memoires.server.service.MemoireService
import com.memoires.server.service.JuryService
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import java.time.LocalDateTime

/**
 * Contrôleur principal pour la page d'accueil et le tableau de bord.
 * 
 * @Controller - Marque cette classe comme un contrôleur Spring MVC
 * Les méthodes annotées avec @GetMapping, @PostMapping, etc. gèrent les requêtes HTTP
 */
@Controller
class HomeController(
    /**
     * Injection des services nécessaires pour récupérer les données du tableau de bord
     */
    private val etudiantService: EtudiantService,
    private val memoireService: MemoireService,
    private val juryService: JuryService
) {
    
    /**
     * Page d'accueil avec tableau de bord.
     * 
     * @GetMapping("/") - Mappe cette méthode à la route racine "/"
     * @param model - Objet Model de Spring pour passer des données à la vue
     * @return String - Nom du template Thymeleaf à rendre
     */
    @GetMapping("/")
    fun home(model: Model): String {
        try {
            // Récupération des statistiques générales
            val totalEtudiants = etudiantService.count()
            val totalMemoires = memoireService.count()
            val totalJurys = juryService.count()
            
            // Statistiques par statut de mémoire
            val memoiresEnCours = memoireService.countByStatut(StatutMemoire.EN_COURS)
            val memoiresDeposes = memoireService.countByStatut(StatutMemoire.DEPOSE)
            val memoiresSoutenus = memoireService.countByStatut(StatutMemoire.SOUTENU)
            
            // Statistiques par niveau d'étude
            val etudiantsLicence = etudiantService.countByNiveauEtude(NiveauEtude.LICENCE)
            val etudiantsMaster = etudiantService.countByNiveauEtude(NiveauEtude.MASTER)
            val etudiantsDoctorat = etudiantService.countByNiveauEtude(NiveauEtude.DOCTORAT)
            
            // Prochaines soutenances (7 prochains jours)
            val dateLimite = LocalDateTime.now().plusDays(7)
            val prochainesSoutenances = memoireService.findProchainesSoutenances(
                dateLimite, PageRequest.of(0, 5)
            )
            
            // Mémoires en attente de planification
            val memoiresAPlanifier = memoireService.findMemoiresPretsAPlanifier(
                PageRequest.of(0, 5)
            )
            
            // Ajout des données au modèle pour la vue
            model.addAttribute("totalEtudiants", totalEtudiants)
            model.addAttribute("totalMemoires", totalMemoires)
            model.addAttribute("totalJurys", totalJurys)
            
            model.addAttribute("memoiresEnCours", memoiresEnCours)
            model.addAttribute("memoiresDeposes", memoiresDeposes)
            model.addAttribute("memoiresSoutenus", memoiresSoutenus)
            
            model.addAttribute("etudiantsLicence", etudiantsLicence)
            model.addAttribute("etudiantsMaster", etudiantsMaster)
            model.addAttribute("etudiantsDoctorat", etudiantsDoctorat)
            
            model.addAttribute("prochainesSoutenances", prochainesSoutenances.content)
            model.addAttribute("memoiresAPlanifier", memoiresAPlanifier.content)
            
            // Données pour les graphiques (format JSON)
            model.addAttribute("statsNiveauxJson", 
                "[{\"niveau\":\"Licence\",\"count\":$etudiantsLicence}," +
                "{\"niveau\":\"Master\",\"count\":$etudiantsMaster}," +
                "{\"niveau\":\"Doctorat\",\"count\":$etudiantsDoctorat}]"
            )
            
            model.addAttribute("statsStatutsJson",
                "[{\"statut\":\"En cours\",\"count\":$memoiresEnCours}," +
                "{\"statut\":\"Déposés\",\"count\":$memoiresDeposes}," +
                "{\"statut\":\"Soutenus\",\"count\":$memoiresSoutenus}]"
            )
            
        } catch (e: Exception) {
            // En cas d'erreur, afficher des valeurs par défaut
            model.addAttribute("error", "Erreur lors du chargement des données: ${e.message}")
            model.addAttribute("totalEtudiants", 0)
            model.addAttribute("totalMemoires", 0)
            model.addAttribute("totalJurys", 0)
        }
        
        // Retourne le nom du template Thymeleaf (src/main/resources/templates/index.html)
        return "index"
    }
    
    /**
     * Page À propos de l'application.
     * 
     * @GetMapping("/about") - Mappe cette méthode à la route "/about"
     */
    @GetMapping("/about")
    fun about(model: Model): String {
        model.addAttribute("appName", "Système de Gestion des Mémoires de Soutenance")
        model.addAttribute("version", "1.0.0")
        model.addAttribute("description", 
            "Application web développée avec Kotlin Multiplatform, Spring Boot, " +
            "PostgreSQL, Thymeleaf et Bootstrap pour la gestion complète des " +
            "mémoires de soutenance dans un établissement académique."
        )
        
        // Technologies utilisées
        val technologies = listOf(
            mapOf("name" to "Kotlin Multiplatform", "description" to "Langage de programmation moderne"),
            mapOf("name" to "Spring Boot", "description" to "Framework Java/Kotlin pour applications web"),
            mapOf("name" to "PostgreSQL", "description" to "Base de données relationnelle"),
            mapOf("name" to "Thymeleaf", "description" to "Moteur de templates pour les vues"),
            mapOf("name" to "Bootstrap", "description" to "Framework CSS pour l'interface utilisateur"),
            mapOf("name" to "JPA/Hibernate", "description" to "ORM pour la persistance des données")
        )
        
        model.addAttribute("technologies", technologies)
        
        return "about"
    }
    
    /**
     * Page de contact/support.
     */
    @GetMapping("/contact")
    fun contact(model: Model): String {
        model.addAttribute("supportEmail", "support@memoires.com")
        model.addAttribute("adminEmail", "admin@memoires.com")
        return "contact"
    }
    
    /**
     * Endpoint pour récupérer les statistiques en format JSON (pour AJAX).
     * Utile pour mettre à jour le tableau de bord sans recharger la page.
     */
    @GetMapping("/api/stats")
    fun getStats(model: Model): String {
        // Cette méthode pourrait retourner du JSON pour des mises à jour dynamiques
        // Pour l'instant, elle redirige vers la page d'accueil
        return "redirect:/"
    }
}