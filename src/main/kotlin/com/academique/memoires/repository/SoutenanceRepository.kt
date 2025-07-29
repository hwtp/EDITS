package com.academique.memoires.repository

import com.academique.memoires.entity.Soutenance
import com.academique.memoires.entity.StatutSoutenance
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface SoutenanceRepository : JpaRepository<Soutenance, Long> {
    
    fun findByMemoireId(memoireId: Long): Soutenance?
    
    fun findByJuryId(juryId: Long): List<Soutenance>
    
    fun findByStatut(statut: StatutSoutenance): List<Soutenance>
    
    fun findBySalle(salle: String): List<Soutenance>
    
    @Query("SELECT s FROM Soutenance s WHERE s.dateSoutenance BETWEEN :dateDebut AND :dateFin")
    fun findByDateSoutenanceBetween(@Param("dateDebut") dateDebut: LocalDateTime, @Param("dateFin") dateFin: LocalDateTime): List<Soutenance>
    
    @Query("SELECT s FROM Soutenance s WHERE DATE(s.dateSoutenance) = DATE(:date)")
    fun findByDateSoutenance(@Param("date") date: LocalDateTime): List<Soutenance>
    
    @Query("SELECT s FROM Soutenance s WHERE s.salle = :salle AND DATE(s.dateSoutenance) = DATE(:date)")
    fun findBySalleAndDate(@Param("salle") salle: String, @Param("date") date: LocalDateTime): List<Soutenance>
    
    @Query("SELECT s FROM Soutenance s WHERE s.statut = :statut ORDER BY s.dateSoutenance ASC")
    fun findByStatutOrderByDateSoutenanceAsc(@Param("statut") statut: StatutSoutenance): List<Soutenance>
    
    @Query("SELECT COUNT(s) FROM Soutenance s WHERE s.jury.id = :juryId AND s.statut = :statut")
    fun countByJuryIdAndStatut(@Param("juryId") juryId: Long, @Param("statut") statut: StatutSoutenance): Long
}