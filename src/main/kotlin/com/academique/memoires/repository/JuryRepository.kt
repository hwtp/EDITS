package com.academique.memoires.repository

import com.academique.memoires.entity.Jury
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface JuryRepository : JpaRepository<Jury, Long> {
    
    fun findByPresidentId(presidentId: Long): List<Jury>
    
    @Query("SELECT j FROM Jury j JOIN j.membres m WHERE m.id = :membreId")
    fun findByMembreId(@Param("membreId") membreId: Long): List<Jury>
    
    @Query("SELECT j FROM Jury j WHERE j.dateCreation BETWEEN :dateDebut AND :dateFin")
    fun findByDateCreationBetween(@Param("dateDebut") dateDebut: LocalDate, @Param("dateFin") dateFin: LocalDate): List<Jury>
    
    @Query("SELECT COUNT(j) FROM Jury j WHERE j.president.id = :presidentId")
    fun countByPresidentId(@Param("presidentId") presidentId: Long): Long
}