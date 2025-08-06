package com.gestionmemoire.repository

import com.gestionmemoire.model.Utilisateur
import org.springframework.data.jpa.repository.JpaRepository

interface UtilisateurRepository : JpaRepository<Utilisateur, Long> {
    fun findByEmail(email: String): Utilisateur?
}