package com.memoires.server.service

import com.memoires.server.entity.Etudiant
import com.memoires.server.entity.NiveauEtude
import com.memoires.server.repository.EtudiantRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Service pour la gestion des étudiants.
 * 
 * @Service - Marque cette classe comme un service Spring (composant métier)
 * @Transactional - Gère automatiquement les transactions de base de données
 */
@Service
@Transactional
class EtudiantService(
    /**
     * Injection de dépendance du repository étudiant.
     * Spring injecte automatiquement l'implémentation du repository.
     */
    private val etudiantRepository: EtudiantRepository
) {
    
    /**
     * Récupère tous les étudiants avec pagination.
     * 
     * @param pageable Informations de pagination (page, taille, tri)
     * @return Page<Etudiant> - Page d'étudiants
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<Etudiant> {
        return etudiantRepository.findAll(pageable)
    }
    
    /**
     * Récupère un étudiant par son ID.
     * 
     * @param id L'identifiant de l'étudiant
     * @return Optional<Etudiant> - Optionnel contenant l'étudiant s'il existe
     */
    @Transactional(readOnly = true)
    fun findById(id: Long): Optional<Etudiant> {
        return etudiantRepository.findById(id)
    }
    
    /**
     * Récupère un étudiant par son numéro d'étudiant.
     * 
     * @param numeroEtudiant Le numéro unique de l'étudiant
     * @return Optional<Etudiant> - Optionnel contenant l'étudiant s'il existe
     */
    @Transactional(readOnly = true)
    fun findByNumeroEtudiant(numeroEtudiant: String): Optional<Etudiant> {
        return etudiantRepository.findByNumeroEtudiant(numeroEtudiant)
    }
    
    /**
     * Récupère un étudiant par son email.
     * 
     * @param email L'adresse email de l'étudiant
     * @return Optional<Etudiant> - Optionnel contenant l'étudiant s'il existe
     */
    @Transactional(readOnly = true)
    fun findByEmail(email: String): Optional<Etudiant> {
        return etudiantRepository.findByEmail(email)
    }
    
    /**
     * Crée ou met à jour un étudiant.
     * Effectue les validations métier avant la sauvegarde.
     * 
     * @param etudiant L'étudiant à sauvegarder
     * @return Etudiant - L'étudiant sauvegardé avec son ID généré
     * @throws IllegalArgumentException si les données sont invalides
     */
    fun save(etudiant: Etudiant): Etudiant {
        // Validation métier : vérifier l'unicité du numéro étudiant
        if (etudiant.id == null && etudiantRepository.existsByNumeroEtudiant(etudiant.numeroEtudiant)) {
            throw IllegalArgumentException("Le numéro étudiant '${etudiant.numeroEtudiant}' existe déjà")
        }
        
        // Validation métier : vérifier l'unicité de l'email
        if (etudiant.id == null && etudiantRepository.existsByEmail(etudiant.email)) {
            throw IllegalArgumentException("L'email '${etudiant.email}' existe déjà")
        }
        
        // Pour les mises à jour, vérifier que les changements ne créent pas de doublons
        if (etudiant.id != null) {
            val existingByNumero = etudiantRepository.findByNumeroEtudiant(etudiant.numeroEtudiant)
            if (existingByNumero.isPresent && existingByNumero.get().id != etudiant.id) {
                throw IllegalArgumentException("Le numéro étudiant '${etudiant.numeroEtudiant}' existe déjà")
            }
            
            val existingByEmail = etudiantRepository.findByEmail(etudiant.email)
            if (existingByEmail.isPresent && existingByEmail.get().id != etudiant.id) {
                throw IllegalArgumentException("L'email '${etudiant.email}' existe déjà")
            }
        }
        
        return etudiantRepository.save(etudiant)
    }
    
    /**
     * Supprime un étudiant par son ID.
     * Vérifie qu'il n'a pas de mémoires associés avant la suppression.
     * 
     * @param id L'identifiant de l'étudiant à supprimer
     * @throws IllegalStateException si l'étudiant a des mémoires associés
     * @throws NoSuchElementException si l'étudiant n'existe pas
     */
    fun deleteById(id: Long) {
        val etudiant = etudiantRepository.findById(id)
            .orElseThrow { NoSuchElementException("Étudiant non trouvé avec l'ID: $id") }
        
        // Vérification métier : ne pas supprimer un étudiant ayant des mémoires
        if (etudiant.memoires.isNotEmpty()) {
            throw IllegalStateException("Impossible de supprimer un étudiant ayant des mémoires associés")
        }
        
        etudiantRepository.deleteById(id)
    }
    
    /**
     * Recherche des étudiants par niveau d'étude.
     * 
     * @param niveauEtude Le niveau d'étude recherché
     * @param pageable Informations de pagination
     * @return Page<Etudiant> - Page d'étudiants du niveau spécifié
     */
    @Transactional(readOnly = true)
    fun findByNiveauEtude(niveauEtude: NiveauEtude, pageable: Pageable): Page<Etudiant> {
        return etudiantRepository.findByNiveauEtude(niveauEtude, pageable)
    }
    
    /**
     * Recherche des étudiants par filière.
     * 
     * @param filiere La filière recherchée (recherche partielle)
     * @param pageable Informations de pagination
     * @return Page<Etudiant> - Page d'étudiants de la filière
     */
    @Transactional(readOnly = true)
    fun findByFiliere(filiere: String, pageable: Pageable): Page<Etudiant> {
        return etudiantRepository.findByFiliereContainingIgnoreCase(filiere, pageable)
    }
    
    /**
     * Recherche globale d'étudiants par terme de recherche.
     * Recherche dans nom, prénom, email, filière et numéro étudiant.
     * 
     * @param searchTerm Le terme de recherche
     * @param pageable Informations de pagination
     * @return Page<Etudiant> - Page d'étudiants correspondant à la recherche
     */
    @Transactional(readOnly = true)
    fun searchEtudiants(searchTerm: String, pageable: Pageable): Page<Etudiant> {
        return etudiantRepository.searchEtudiants(searchTerm.trim(), pageable)
    }
    
    /**
     * Récupère les étudiants sans mémoire.
     * Utile pour identifier les étudiants qui n'ont pas encore commencé leur mémoire.
     * 
     * @param pageable Informations de pagination
     * @return Page<Etudiant> - Page d'étudiants sans mémoire
     */
    @Transactional(readOnly = true)
    fun findEtudiantsSansMemoire(pageable: Pageable): Page<Etudiant> {
        return etudiantRepository.findEtudiantsSansMemoire(pageable)
    }
    
    /**
     * Compte le nombre total d'étudiants.
     * 
     * @return Long - Nombre total d'étudiants
     */
    @Transactional(readOnly = true)
    fun count(): Long {
        return etudiantRepository.count()
    }
    
    /**
     * Compte les étudiants par niveau d'étude.
     * 
     * @param niveauEtude Le niveau d'étude
     * @return Long - Nombre d'étudiants dans ce niveau
     */
    @Transactional(readOnly = true)
    fun countByNiveauEtude(niveauEtude: NiveauEtude): Long {
        return etudiantRepository.countByNiveauEtude(niveauEtude)
    }
    
    /**
     * Obtient les statistiques par filière.
     * 
     * @return Map<String, Long> - Map contenant [filière -> nombre d'étudiants]
     */
    @Transactional(readOnly = true)
    fun getStatistiquesParFiliere(): Map<String, Long> {
        return etudiantRepository.getStatistiquesParFiliere()
            .associate { it[0] as String to it[1] as Long }
    }
    
    /**
     * Obtient les statistiques par niveau d'étude.
     * 
     * @return Map<NiveauEtude, Long> - Map contenant [niveau -> nombre d'étudiants]
     */
    @Transactional(readOnly = true)
    fun getStatistiquesParNiveau(): Map<NiveauEtude, Long> {
        val stats = mutableMapOf<NiveauEtude, Long>()
        NiveauEtude.values().forEach { niveau ->
            stats[niveau] = countByNiveauEtude(niveau)
        }
        return stats
    }
    
    /**
     * Vérifie si un numéro d'étudiant existe.
     * 
     * @param numeroEtudiant Le numéro à vérifier
     * @return Boolean - true si le numéro existe, false sinon
     */
    @Transactional(readOnly = true)
    fun existsByNumeroEtudiant(numeroEtudiant: String): Boolean {
        return etudiantRepository.existsByNumeroEtudiant(numeroEtudiant)
    }
    
    /**
     * Vérifie si un email existe.
     * 
     * @param email L'email à vérifier
     * @return Boolean - true si l'email existe, false sinon
     */
    @Transactional(readOnly = true)
    fun existsByEmail(email: String): Boolean {
        return etudiantRepository.existsByEmail(email)
    }
    
    /**
     * Valide les données d'un étudiant avant sauvegarde.
     * 
     * @param etudiant L'étudiant à valider
     * @return List<String> - Liste des erreurs de validation (vide si valide)
     */
    fun validateEtudiant(etudiant: Etudiant): List<String> {
        val errors = mutableListOf<String>()
        
        // Validation du numéro étudiant
        if (etudiant.numeroEtudiant.isBlank()) {
            errors.add("Le numéro étudiant est obligatoire")
        } else if (etudiant.numeroEtudiant.length < 5) {
            errors.add("Le numéro étudiant doit contenir au moins 5 caractères")
        }
        
        // Validation du nom et prénom
        if (etudiant.nom.isBlank()) {
            errors.add("Le nom est obligatoire")
        }
        if (etudiant.prenom.isBlank()) {
            errors.add("Le prénom est obligatoire")
        }
        
        // Validation de l'email
        if (etudiant.email.isBlank()) {
            errors.add("L'email est obligatoire")
        } else if (!etudiant.email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))) {
            errors.add("Format d'email invalide")
        }
        
        // Validation de la filière
        if (etudiant.filiere.isBlank()) {
            errors.add("La filière est obligatoire")
        }
        
        return errors
    }
}