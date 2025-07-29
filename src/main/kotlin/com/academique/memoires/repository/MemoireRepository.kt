package com.academique.memoires.repository

import com.academique.memoires.entity.Memoire
import com.academique.memoires.entity.StatutMemoire
import com.academique.memoires.entity.TypeMemoire
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface MemoireRepository : JpaRepository<Memoire, Long> {
    
    fun findByEtudiantId(etudiantId: Long): List<Memoire>
    
    fun findByDirecteurId(directeurId: Long): List<Memoire>
    
    fun findByCoDirecteurId(coDirecteurId: Long): List<Memoire>
    
    fun findByStatut(statut: StatutMemoire): List<Memoire>
    
    fun findByType(type: TypeMemoire): List<Memoire>
    
    @Query("SELECT m FROM Memoire m WHERE LOWER(m.titre) LIKE LOWER(CONCAT('%', :titre, '%'))")
    fun findByTitreContainingIgnoreCase(@Param("titre") titre: String): List<Memoire>
    
    @Query("SELECT m FROM Memoire m WHERE m.dateDepot BETWEEN :dateDebut AND :dateFin")
    fun findByDateDepotBetween(@Param("dateDebut") dateDebut: LocalDate, @Param("dateFin") dateFin: LocalDate): List<Memoire>
    
    @Query("SELECT m FROM Memoire m WHERE m.statut = :statut AND m.type = :type")
    fun findByStatutAndType(@Param("statut") statut: StatutMemoire, @Param("type") type: TypeMemoire): List<Memoire>
    
    @Query("SELECT COUNT(m) FROM Memoire m WHERE m.directeur.id = :directeurId AND m.statut = :statut")
    fun countByDirecteurIdAndStatut(@Param("directeurId") directeurId: Long, @Param("statut") statut: StatutMemoire): Long
    
    @Query("SELECT m FROM Memoire m WHERE LOWER(m.motsCles) LIKE LOWER(CONCAT('%', :motCle, '%'))")
    fun findByMotsClesContainingIgnoreCase(@Param("motCle") motCle: String): List<Memoire>
}