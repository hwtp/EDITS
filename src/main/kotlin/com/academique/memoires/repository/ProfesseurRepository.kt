package com.academique.memoires.repository

import com.academique.memoires.entity.GradeProfesseur
import com.academique.memoires.entity.Professeur
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ProfesseurRepository : JpaRepository<Professeur, Long> {
    
    fun findByEmail(email: String): Professeur?
    
    fun findByDepartement(departement: String): List<Professeur>
    
    fun findBySpecialite(specialite: String): List<Professeur>
    
    fun findByGrade(grade: GradeProfesseur): List<Professeur>
    
    @Query("SELECT p FROM Professeur p WHERE LOWER(p.nom) LIKE LOWER(CONCAT('%', :nom, '%')) OR LOWER(p.prenom) LIKE LOWER(CONCAT('%', :nom, '%'))")
    fun findByNomOrPrenomContainingIgnoreCase(@Param("nom") nom: String): List<Professeur>
    
    @Query("SELECT p FROM Professeur p WHERE p.departement = :departement AND p.specialite = :specialite")
    fun findByDepartementAndSpecialite(@Param("departement") departement: String, @Param("specialite") specialite: String): List<Professeur>
    
    fun existsByEmail(email: String): Boolean
}