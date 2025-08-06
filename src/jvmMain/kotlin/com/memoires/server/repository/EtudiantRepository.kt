package com.memoires.server.repository

import com.memoires.server.entity.Etudiant
import com.memoires.server.entity.NiveauEtude
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Repository pour l'entité Etudiant.
 * 
 * @Repository - Marque cette interface comme un composant de persistance Spring
 * JpaRepository<Etudiant, Long> - Fournit les opérations CRUD de base
 * - Etudiant : Type de l'entité
 * - Long : Type de la clé primaire
 */
@Repository
interface EtudiantRepository : JpaRepository<Etudiant, Long> {
    
    /**
     * Recherche un étudiant par son numéro d'étudiant.
     * Spring Data JPA génère automatiquement la requête basée sur le nom de la méthode.
     * 
     * @param numeroEtudiant Le numéro unique de l'étudiant
     * @return Optional<Etudiant> - Optionnel contenant l'étudiant s'il existe
     */
    fun findByNumeroEtudiant(numeroEtudiant: String): Optional<Etudiant>
    
    /**
     * Recherche un étudiant par son email.
     * 
     * @param email L'adresse email de l'étudiant
     * @return Optional<Etudiant> - Optionnel contenant l'étudiant s'il existe
     */
    fun findByEmail(email: String): Optional<Etudiant>
    
    /**
     * Vérifie si un numéro d'étudiant existe déjà.
     * 
     * @param numeroEtudiant Le numéro à vérifier
     * @return Boolean - true si le numéro existe, false sinon
     */
    fun existsByNumeroEtudiant(numeroEtudiant: String): Boolean
    
    /**
     * Vérifie si un email existe déjà.
     * 
     * @param email L'email à vérifier
     * @return Boolean - true si l'email existe, false sinon
     */
    fun existsByEmail(email: String): Boolean
    
    /**
     * Recherche des étudiants par niveau d'étude avec pagination.
     * 
     * @param niveauEtude Le niveau d'étude recherché
     * @param pageable Informations de pagination
     * @return Page<Etudiant> - Page d'étudiants correspondant au niveau
     */
    fun findByNiveauEtude(niveauEtude: NiveauEtude, pageable: Pageable): Page<Etudiant>
    
    /**
     * Recherche des étudiants par filière avec pagination.
     * 
     * @param filiere La filière recherchée
     * @param pageable Informations de pagination
     * @return Page<Etudiant> - Page d'étudiants de la filière
     */
    fun findByFiliereContainingIgnoreCase(filiere: String, pageable: Pageable): Page<Etudiant>
    
    /**
     * Recherche des étudiants par nom ou prénom (recherche partielle insensible à la casse).
     * 
     * @param nom Le nom à rechercher
     * @param prenom Le prénom à rechercher
     * @param pageable Informations de pagination
     * @return Page<Etudiant> - Page d'étudiants correspondant aux critères
     */
    fun findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
        nom: String, 
        prenom: String, 
        pageable: Pageable
    ): Page<Etudiant>
    
    /**
     * Requête personnalisée pour rechercher des étudiants par plusieurs critères.
     * @Query - Annotation pour définir une requête JPQL personnalisée
     * 
     * @param searchTerm Terme de recherche qui sera appliqué sur nom, prénom, email et filière
     * @param pageable Informations de pagination
     * @return Page<Etudiant> - Page d'étudiants correspondant à la recherche
     */
    @Query("""
        SELECT e FROM Etudiant e 
        WHERE LOWER(e.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR LOWER(e.prenom) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR LOWER(e.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR LOWER(e.filiere) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR LOWER(e.numeroEtudiant) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
    """)
    fun searchEtudiants(@Param("searchTerm") searchTerm: String, pageable: Pageable): Page<Etudiant>
    
    /**
     * Compte le nombre d'étudiants par niveau d'étude.
     * 
     * @param niveauEtude Le niveau d'étude
     * @return Long - Nombre d'étudiants dans ce niveau
     */
    fun countByNiveauEtude(niveauEtude: NiveauEtude): Long
    
    /**
     * Requête personnalisée pour obtenir les étudiants sans mémoire.
     * 
     * @param pageable Informations de pagination
     * @return Page<Etudiant> - Page d'étudiants n'ayant pas de mémoire
     */
    @Query("""
        SELECT e FROM Etudiant e 
        WHERE e.id NOT IN (SELECT DISTINCT m.etudiant.id FROM Memoire m)
    """)
    fun findEtudiantsSansMemoire(pageable: Pageable): Page<Etudiant>
    
    /**
     * Requête pour obtenir les statistiques par filière.
     * 
     * @return List<Array<Any>> - Liste contenant [filière, nombre d'étudiants]
     */
    @Query("""
        SELECT e.filiere, COUNT(e) 
        FROM Etudiant e 
        GROUP BY e.filiere 
        ORDER BY COUNT(e) DESC
    """)
    fun getStatistiquesParFiliere(): List<Array<Any>>
}