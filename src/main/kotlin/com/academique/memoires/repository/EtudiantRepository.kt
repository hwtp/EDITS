package com.academique.memoires.repository

import com.academique.memoires.entity.Etudiant
import com.academique.memoires.entity.NiveauEtude
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface EtudiantRepository : JpaRepository<Etudiant, Long> {
    
    fun findByEmail(email: String): Etudiant?
    
    fun findByNumeroEtudiant(numeroEtudiant: String): Etudiant?
    
    fun findByFiliere(filiere: String): List<Etudiant>
    
    fun findByNiveau(niveau: NiveauEtude): List<Etudiant>
    
    @Query("SELECT e FROM Etudiant e WHERE LOWER(e.nom) LIKE LOWER(CONCAT('%', :nom, '%')) OR LOWER(e.prenom) LIKE LOWER(CONCAT('%', :nom, '%'))")
    fun findByNomOrPrenomContainingIgnoreCase(@Param("nom") nom: String): List<Etudiant>
    
    fun existsByEmail(email: String): Boolean
    
    fun existsByNumeroEtudiant(numeroEtudiant: String): Boolean
}