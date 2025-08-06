package com.memoires.server.repository

import com.memoires.server.entity.Memoire
import com.memoires.server.entity.StatutMemoire
import com.memoires.server.entity.TypeMemoire
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

/**
 * Repository pour l'entité Memoire.
 * Fournit les méthodes d'accès aux données pour les mémoires de soutenance.
 */
@Repository
interface MemoireRepository : JpaRepository<Memoire, Long> {
    
    /**
     * Recherche des mémoires par statut avec pagination.
     * 
     * @param statut Le statut recherché
     * @param pageable Informations de pagination
     * @return Page<Memoire> - Page de mémoires ayant ce statut
     */
    fun findByStatut(statut: StatutMemoire, pageable: Pageable): Page<Memoire>
    
    /**
     * Recherche des mémoires par type avec pagination.
     * 
     * @param typeMemoire Le type de mémoire recherché
     * @param pageable Informations de pagination
     * @return Page<Memoire> - Page de mémoires de ce type
     */
    fun findByTypeMemoire(typeMemoire: TypeMemoire, pageable: Pageable): Page<Memoire>
    
    /**
     * Recherche des mémoires par année académique avec pagination.
     * 
     * @param anneeAcademique L'année académique recherchée (format: 2023-2024)
     * @param pageable Informations de pagination
     * @return Page<Memoire> - Page de mémoires de cette année
     */
    fun findByAnneeAcademique(anneeAcademique: String, pageable: Pageable): Page<Memoire>
    
    /**
     * Recherche des mémoires par étudiant.
     * 
     * @param etudiantId ID de l'étudiant
     * @param pageable Informations de pagination
     * @return Page<Memoire> - Page des mémoires de cet étudiant
     */
    fun findByEtudiantId(etudiantId: Long, pageable: Pageable): Page<Memoire>
    
    /**
     * Recherche des mémoires par directeur.
     * 
     * @param directeurId ID du directeur
     * @param pageable Informations de pagination
     * @return Page<Memoire> - Page des mémoires dirigés par cet enseignant
     */
    fun findByDirecteurId(directeurId: Long, pageable: Pageable): Page<Memoire>
    
    /**
     * Recherche globale de mémoires par titre, mots-clés ou résumé.
     * 
     * @param searchTerm Terme de recherche
     * @param pageable Informations de pagination
     * @return Page<Memoire> - Page de mémoires correspondant à la recherche
     */
    @Query("""
        SELECT m FROM Memoire m 
        WHERE LOWER(m.titre) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR LOWER(m.motsCles) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR LOWER(m.resume) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR LOWER(m.etudiant.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR LOWER(m.etudiant.prenom) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR LOWER(m.directeur.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR LOWER(m.directeur.prenom) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
    """)
    fun searchMemoires(@Param("searchTerm") searchTerm: String, pageable: Pageable): Page<Memoire>
    
    /**
     * Recherche des mémoires déposés mais pas encore planifiés pour soutenance.
     * 
     * @param pageable Informations de pagination
     * @return Page<Memoire> - Page de mémoires prêts à être planifiés
     */
    @Query("""
        SELECT m FROM Memoire m 
        WHERE m.statut = 'DEPOSE' 
        AND m.cheminFichier IS NOT NULL
        AND m.jury IS NULL
        ORDER BY m.dateDepot ASC
    """)
    fun findMemoiresPretsAPlanifier(pageable: Pageable): Page<Memoire>
    
    /**
     * Recherche des soutenances programmées dans une période donnée.
     * 
     * @param dateDebut Date de début de la période
     * @param dateFin Date de fin de la période
     * @param pageable Informations de pagination
     * @return Page<Memoire> - Page de mémoires avec soutenance dans cette période
     */
    @Query("""
        SELECT m FROM Memoire m 
        JOIN m.jury j 
        WHERE j.dateHeureSoutenance BETWEEN :dateDebut AND :dateFin
        ORDER BY j.dateHeureSoutenance ASC
    """)
    fun findSoutenancesDansPeriode(
        @Param("dateDebut") dateDebut: LocalDateTime,
        @Param("dateFin") dateFin: LocalDateTime,
        pageable: Pageable
    ): Page<Memoire>
    
    /**
     * Recherche des mémoires soutenus avec mention.
     * 
     * @param pageable Informations de pagination
     * @return Page<Memoire> - Page de mémoires soutenus avec leur mention
     */
    @Query("""
        SELECT m FROM Memoire m 
        WHERE m.statut = 'SOUTENU' 
        AND m.mention IS NOT NULL
        ORDER BY m.dateSoutenanceReelle DESC
    """)
    fun findMemoiresSoutenus(pageable: Pageable): Page<Memoire>
    
    /**
     * Compte les mémoires par statut.
     * 
     * @param statut Le statut à compter
     * @return Long - Nombre de mémoires avec ce statut
     */
    fun countByStatut(statut: StatutMemoire): Long
    
    /**
     * Compte les mémoires par type.
     * 
     * @param typeMemoire Le type à compter
     * @return Long - Nombre de mémoires de ce type
     */
    fun countByTypeMemoire(typeMemoire: TypeMemoire): Long
    
    /**
     * Obtient les statistiques par statut.
     * 
     * @return List<Array<Any>> - Liste contenant [statut, nombre de mémoires]
     */
    @Query("""
        SELECT m.statut, COUNT(m) 
        FROM Memoire m 
        GROUP BY m.statut 
        ORDER BY COUNT(m) DESC
    """)
    fun getStatistiquesParStatut(): List<Array<Any>>
    
    /**
     * Obtient les statistiques par type de mémoire.
     * 
     * @return List<Array<Any>> - Liste contenant [type, nombre de mémoires]
     */
    @Query("""
        SELECT m.typeMemoire, COUNT(m) 
        FROM Memoire m 
        GROUP BY m.typeMemoire 
        ORDER BY COUNT(m) DESC
    """)
    fun getStatistiquesParType(): List<Array<Any>>
    
    /**
     * Obtient les statistiques par année académique.
     * 
     * @return List<Array<Any>> - Liste contenant [année, nombre de mémoires]
     */
    @Query("""
        SELECT m.anneeAcademique, COUNT(m) 
        FROM Memoire m 
        GROUP BY m.anneeAcademique 
        ORDER BY m.anneeAcademique DESC
    """)
    fun getStatistiquesParAnnee(): List<Array<Any>>
    
    /**
     * Obtient les mémoires en retard (déposés depuis plus de X jours sans planification).
     * 
     * @param joursRetard Nombre de jours considérés comme retard
     * @param pageable Informations de pagination
     * @return Page<Memoire> - Page de mémoires en retard
     */
    @Query("""
        SELECT m FROM Memoire m 
        WHERE m.statut = 'DEPOSE' 
        AND m.dateDepot < :dateRetard
        AND m.jury IS NULL
        ORDER BY m.dateDepot ASC
    """)
    fun findMemoiresEnRetard(
        @Param("dateRetard") dateRetard: LocalDateTime,
        pageable: Pageable
    ): Page<Memoire>
    
    /**
     * Recherche des mémoires par filière de l'étudiant.
     * 
     * @param filiere La filière recherchée
     * @param pageable Informations de pagination
     * @return Page<Memoire> - Page de mémoires d'étudiants de cette filière
     */
    @Query("""
        SELECT m FROM Memoire m 
        WHERE LOWER(m.etudiant.filiere) LIKE LOWER(CONCAT('%', :filiere, '%'))
        ORDER BY m.dateCreation DESC
    """)
    fun findByFiliereEtudiant(@Param("filiere") filiere: String, pageable: Pageable): Page<Memoire>
    
    /**
     * Obtient la note moyenne par type de mémoire.
     * 
     * @return List<Array<Any>> - Liste contenant [type, note moyenne]
     */
    @Query("""
        SELECT m.typeMemoire, AVG(m.note) 
        FROM Memoire m 
        WHERE m.note IS NOT NULL
        GROUP BY m.typeMemoire
    """)
    fun getNoteMoyenneParType(): List<Array<Any>>
    
    /**
     * Trouve les prochaines soutenances (dans les 7 prochains jours).
     * 
     * @param pageable Informations de pagination
     * @return Page<Memoire> - Page des prochaines soutenances
     */
    @Query("""
        SELECT m FROM Memoire m 
        JOIN m.jury j 
        WHERE j.dateHeureSoutenance BETWEEN CURRENT_TIMESTAMP AND :dateLimite
        AND j.statut NOT IN ('ANNULE', 'TERMINE')
        ORDER BY j.dateHeureSoutenance ASC
    """)
    fun findProchainesSoutenances(
        @Param("dateLimite") dateLimite: LocalDateTime,
        pageable: Pageable
    ): Page<Memoire>
}