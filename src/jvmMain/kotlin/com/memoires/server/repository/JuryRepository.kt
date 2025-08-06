package com.memoires.server.repository

import com.memoires.server.entity.Jury
import com.memoires.server.entity.StatutJury
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

/**
 * Repository pour l'entité Jury.
 * Fournit les méthodes d'accès aux données pour les jurys de soutenance.
 */
@Repository
interface JuryRepository : JpaRepository<Jury, Long> {
    
    /**
     * Recherche des jurys par statut avec pagination.
     * 
     * @param statut Le statut recherché
     * @param pageable Informations de pagination
     * @return Page<Jury> - Page de jurys ayant ce statut
     */
    fun findByStatut(statut: StatutJury, pageable: Pageable): Page<Jury>
    
    /**
     * Recherche un jury par l'ID du mémoire associé.
     * 
     * @param memoireId ID du mémoire
     * @return Optional<Jury> - Optionnel contenant le jury s'il existe
     */
    fun findByMemoireId(memoireId: Long): Optional<Jury>
    
    /**
     * Recherche des jurys présidés par un enseignant spécifique.
     * 
     * @param presidentId ID du président
     * @param pageable Informations de pagination
     * @return Page<Jury> - Page de jurys présidés par cet enseignant
     */
    fun findByPresidentId(presidentId: Long, pageable: Pageable): Page<Jury>
    
    /**
     * Recherche des jurys dans lesquels un enseignant est membre.
     * 
     * @param enseignantId ID de l'enseignant
     * @param pageable Informations de pagination
     * @return Page<Jury> - Page de jurys où cet enseignant est membre
     */
    @Query("""
        SELECT j FROM Jury j 
        JOIN j.membres m 
        WHERE m.id = :enseignantId
        ORDER BY j.dateHeureSoutenance DESC
    """)
    fun findByMembreId(@Param("enseignantId") enseignantId: Long, pageable: Pageable): Page<Jury>
    
    /**
     * Recherche des jurys programmés dans une période donnée.
     * 
     * @param dateDebut Date de début de la période
     * @param dateFin Date de fin de la période
     * @param pageable Informations de pagination
     * @return Page<Jury> - Page de jurys dans cette période
     */
    fun findByDateHeureSoutenanceBetween(
        dateDebut: LocalDateTime, 
        dateFin: LocalDateTime, 
        pageable: Pageable
    ): Page<Jury>
    
    /**
     * Recherche des jurys programmés pour aujourd'hui.
     * 
     * @param dateDebut Début de la journée
     * @param dateFin Fin de la journée
     * @param pageable Informations de pagination
     * @return Page<Jury> - Page de jurys d'aujourd'hui
     */
    @Query("""
        SELECT j FROM Jury j 
        WHERE j.dateHeureSoutenance BETWEEN :dateDebut AND :dateFin
        AND j.statut NOT IN ('ANNULE', 'TERMINE')
        ORDER BY j.dateHeureSoutenance ASC
    """)
    fun findJurysAujourdhui(
        @Param("dateDebut") dateDebut: LocalDateTime,
        @Param("dateFin") dateFin: LocalDateTime,
        pageable: Pageable
    ): Page<Jury>
    
    /**
     * Recherche des jurys en conflit d'horaire avec un créneau donné.
     * Utilisé pour vérifier la disponibilité d'une salle ou d'enseignants.
     * 
     * @param dateDebut Date et heure de début du créneau
     * @param dateFin Date et heure de fin du créneau
     * @param juryIdExclu ID du jury à exclure (pour les modifications)
     * @param pageable Informations de pagination
     * @return Page<Jury> - Page de jurys en conflit
     */
    @Query("""
        SELECT j FROM Jury j 
        WHERE j.dateHeureSoutenance BETWEEN :dateDebut AND :dateFin
        AND (:juryIdExclu IS NULL OR j.id != :juryIdExclu)
        AND j.statut NOT IN ('ANNULE', 'TERMINE')
        ORDER BY j.dateHeureSoutenance ASC
    """)
    fun findConflitsHoraire(
        @Param("dateDebut") dateDebut: LocalDateTime,
        @Param("dateFin") dateFin: LocalDateTime,
        @Param("juryIdExclu") juryIdExclu: Long?,
        pageable: Pageable
    ): Page<Jury>
    
    /**
     * Recherche des jurys utilisant un lieu spécifique dans une période.
     * 
     * @param lieu Le lieu recherché
     * @param dateDebut Date de début de la période
     * @param dateFin Date de fin de la période
     * @param pageable Informations de pagination
     * @return Page<Jury> - Page de jurys utilisant ce lieu
     */
    @Query("""
        SELECT j FROM Jury j 
        WHERE LOWER(j.lieu) = LOWER(:lieu)
        AND j.dateHeureSoutenance BETWEEN :dateDebut AND :dateFin
        AND j.statut NOT IN ('ANNULE', 'TERMINE')
        ORDER BY j.dateHeureSoutenance ASC
    """)
    fun findByLieuEtPeriode(
        @Param("lieu") lieu: String,
        @Param("dateDebut") dateDebut: LocalDateTime,
        @Param("dateFin") dateFin: LocalDateTime,
        pageable: Pageable
    ): Page<Jury>
    
    /**
     * Recherche des jurys incomplets (moins de 3 membres).
     * 
     * @param pageable Informations de pagination
     * @return Page<Jury> - Page de jurys incomplets
     */
    @Query("""
        SELECT j FROM Jury j 
        WHERE SIZE(j.membres) < 3
        ORDER BY j.dateHeureSoutenance ASC
    """)
    fun findJurysIncomplets(pageable: Pageable): Page<Jury>
    
    /**
     * Compte les jurys par statut.
     * 
     * @param statut Le statut à compter
     * @return Long - Nombre de jurys avec ce statut
     */
    fun countByStatut(statut: StatutJury): Long
    
    /**
     * Obtient les statistiques par statut de jury.
     * 
     * @return List<Array<Any>> - Liste contenant [statut, nombre de jurys]
     */
    @Query("""
        SELECT j.statut, COUNT(j) 
        FROM Jury j 
        GROUP BY j.statut 
        ORDER BY COUNT(j) DESC
    """)
    fun getStatistiquesParStatut(): List<Array<Any>>
    
    /**
     * Obtient les lieux les plus utilisés pour les soutenances.
     * 
     * @return List<Array<Any>> - Liste contenant [lieu, nombre d'utilisations]
     */
    @Query("""
        SELECT j.lieu, COUNT(j) 
        FROM Jury j 
        GROUP BY j.lieu 
        ORDER BY COUNT(j) DESC
    """)
    fun getLieuxLesPlusUtilises(): List<Array<Any>>
    
    /**
     * Obtient la charge de travail des enseignants (nombre de jurys par enseignant).
     * 
     * @return List<Array<Any>> - Liste contenant [enseignant, nombre de jurys]
     */
    @Query("""
        SELECT m, COUNT(j) as nbJurys 
        FROM Jury j 
        JOIN j.membres m 
        GROUP BY m 
        ORDER BY COUNT(j) DESC, m.nom ASC
    """)
    fun getChargeTravailEnseignants(): List<Array<Any>>
    
    /**
     * Trouve les créneaux libres dans une période pour un lieu donné.
     * 
     * @param lieu Le lieu recherché
     * @param dateDebut Date de début de la période
     * @param dateFin Date de fin de la période
     * @return List<Jury> - Liste des jurys occupant ce lieu dans cette période
     */
    @Query("""
        SELECT j FROM Jury j 
        WHERE LOWER(j.lieu) = LOWER(:lieu)
        AND j.dateHeureSoutenance BETWEEN :dateDebut AND :dateFin
        AND j.statut NOT IN ('ANNULE', 'TERMINE')
        ORDER BY j.dateHeureSoutenance ASC
    """)
    fun findOccupationsLieu(
        @Param("lieu") lieu: String,
        @Param("dateDebut") dateDebut: LocalDateTime,
        @Param("dateFin") dateFin: LocalDateTime
    ): List<Jury>
    
    /**
     * Trouve les prochains jurys d'un enseignant.
     * 
     * @param enseignantId ID de l'enseignant
     * @param pageable Informations de pagination
     * @return Page<Jury> - Page des prochains jurys de cet enseignant
     */
    @Query("""
        SELECT j FROM Jury j 
        JOIN j.membres m 
        WHERE m.id = :enseignantId
        AND j.dateHeureSoutenance > CURRENT_TIMESTAMP
        AND j.statut NOT IN ('ANNULE', 'TERMINE')
        ORDER BY j.dateHeureSoutenance ASC
    """)
    fun findProchainsJurysEnseignant(
        @Param("enseignantId") enseignantId: Long,
        pageable: Pageable
    ): Page<Jury>
    
    /**
     * Recherche des jurys par type de mémoire.
     * 
     * @param typeMemoire Le type de mémoire
     * @param pageable Informations de pagination
     * @return Page<Jury> - Page de jurys pour ce type de mémoire
     */
    @Query("""
        SELECT j FROM Jury j 
        WHERE j.memoire.typeMemoire = :typeMemoire
        ORDER BY j.dateHeureSoutenance DESC
    """)
    fun findByTypeMemoire(
        @Param("typeMemoire") typeMemoire: com.memoires.server.entity.TypeMemoire,
        pageable: Pageable
    ): Page<Jury>
}