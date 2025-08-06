package com.memoires.server.repository

import com.memoires.server.entity.Enseignant
import com.memoires.server.entity.GradeEnseignant
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Repository pour l'entité Enseignant.
 * Fournit les méthodes d'accès aux données pour les enseignants.
 */
@Repository
interface EnseignantRepository : JpaRepository<Enseignant, Long> {
    
    /**
     * Recherche un enseignant par son email.
     * 
     * @param email L'adresse email de l'enseignant
     * @return Optional<Enseignant> - Optionnel contenant l'enseignant s'il existe
     */
    fun findByEmail(email: String): Optional<Enseignant>
    
    /**
     * Vérifie si un email existe déjà.
     * 
     * @param email L'email à vérifier
     * @return Boolean - true si l'email existe, false sinon
     */
    fun existsByEmail(email: String): Boolean
    
    /**
     * Recherche des enseignants par grade avec pagination.
     * 
     * @param grade Le grade recherché
     * @param pageable Informations de pagination
     * @return Page<Enseignant> - Page d'enseignants ayant ce grade
     */
    fun findByGrade(grade: GradeEnseignant, pageable: Pageable): Page<Enseignant>
    
    /**
     * Recherche des enseignants par département avec pagination.
     * 
     * @param departement Le département recherché
     * @param pageable Informations de pagination
     * @return Page<Enseignant> - Page d'enseignants du département
     */
    fun findByDepartementContainingIgnoreCase(departement: String, pageable: Pageable): Page<Enseignant>
    
    /**
     * Recherche des enseignants par spécialité avec pagination.
     * 
     * @param specialite La spécialité recherchée
     * @param pageable Informations de pagination
     * @return Page<Enseignant> - Page d'enseignants de cette spécialité
     */
    fun findBySpecialiteContainingIgnoreCase(specialite: String, pageable: Pageable): Page<Enseignant>
    
    /**
     * Recherche globale d'enseignants par nom, prénom, email, spécialité ou département.
     * 
     * @param searchTerm Terme de recherche
     * @param pageable Informations de pagination
     * @return Page<Enseignant> - Page d'enseignants correspondant à la recherche
     */
    @Query("""
        SELECT e FROM Enseignant e 
        WHERE LOWER(e.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR LOWER(e.prenom) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR LOWER(e.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR LOWER(e.specialite) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR LOWER(e.departement) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
    """)
    fun searchEnseignants(@Param("searchTerm") searchTerm: String, pageable: Pageable): Page<Enseignant>
    
    /**
     * Recherche des enseignants éligibles pour être président de jury.
     * Seuls les Professeurs et Maîtres de conférences peuvent présider un jury.
     * 
     * @param pageable Informations de pagination
     * @return Page<Enseignant> - Page d'enseignants éligibles pour présider
     */
    @Query("""
        SELECT e FROM Enseignant e 
        WHERE e.grade IN ('PROFESSEUR', 'MAITRE_CONFERENCES')
        ORDER BY e.grade ASC, e.nom ASC
    """)
    fun findEligiblesPresidentJury(pageable: Pageable): Page<Enseignant>
    
    /**
     * Recherche des enseignants disponibles pour un jury à une date donnée.
     * Exclut les enseignants déjà occupés par un autre jury au même moment.
     * 
     * @param dateDebut Date et heure de début de la soutenance
     * @param dateFin Date et heure de fin de la soutenance
     * @param juryIdExclu ID du jury à exclure (pour les modifications)
     * @param pageable Informations de pagination
     * @return Page<Enseignant> - Page d'enseignants disponibles
     */
    @Query("""
        SELECT e FROM Enseignant e 
        WHERE e.id NOT IN (
            SELECT DISTINCT em.id FROM Jury j 
            JOIN j.membres em 
            WHERE j.dateHeureSoutenance BETWEEN :dateDebut AND :dateFin
            AND (:juryIdExclu IS NULL OR j.id != :juryIdExclu)
            AND j.statut NOT IN ('ANNULE', 'TERMINE')
        )
        ORDER BY e.grade ASC, e.nom ASC
    """)
    fun findDisponiblesPourJury(
        @Param("dateDebut") dateDebut: java.time.LocalDateTime,
        @Param("dateFin") dateFin: java.time.LocalDateTime,
        @Param("juryIdExclu") juryIdExclu: Long?,
        pageable: Pageable
    ): Page<Enseignant>
    
    /**
     * Compte le nombre de mémoires dirigés par un enseignant.
     * 
     * @param enseignantId ID de l'enseignant
     * @return Long - Nombre de mémoires dirigés
     */
    @Query("""
        SELECT COUNT(m) FROM Memoire m 
        WHERE m.directeur.id = :enseignantId
    """)
    fun countMemoiresDiriges(@Param("enseignantId") enseignantId: Long): Long
    
    /**
     * Compte le nombre de jurys auxquels un enseignant participe.
     * 
     * @param enseignantId ID de l'enseignant
     * @return Long - Nombre de jurys
     */
    @Query("""
        SELECT COUNT(j) FROM Jury j 
        JOIN j.membres m 
        WHERE m.id = :enseignantId
    """)
    fun countJurysParticipation(@Param("enseignantId") enseignantId: Long): Long
    
    /**
     * Obtient les statistiques par grade.
     * 
     * @return List<Array<Any>> - Liste contenant [grade, nombre d'enseignants]
     */
    @Query("""
        SELECT e.grade, COUNT(e) 
        FROM Enseignant e 
        GROUP BY e.grade 
        ORDER BY COUNT(e) DESC
    """)
    fun getStatistiquesParGrade(): List<Array<Any>>
    
    /**
     * Obtient les statistiques par département.
     * 
     * @return List<Array<Any>> - Liste contenant [département, nombre d'enseignants]
     */
    @Query("""
        SELECT e.departement, COUNT(e) 
        FROM Enseignant e 
        GROUP BY e.departement 
        ORDER BY COUNT(e) DESC
    """)
    fun getStatistiquesParDepartement(): List<Array<Any>>
    
    /**
     * Trouve les enseignants les plus actifs (ayant le plus de mémoires dirigés).
     * 
     * @param pageable Informations de pagination
     * @return Page<Array<Any>> - Page contenant [enseignant, nombre de mémoires]
     */
    @Query("""
        SELECT e, COUNT(m) as nbMemoires 
        FROM Enseignant e 
        LEFT JOIN e.memoiresDiriges m 
        GROUP BY e 
        ORDER BY COUNT(m) DESC, e.nom ASC
    """)
    fun findEnseignantsLesPlus​Actifs(pageable: Pageable): Page<Array<Any>>
}