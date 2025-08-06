package com.gestionmemoire.model

import jakarta.persistence.*

@Entity
@Table(name = "utilisateur")
data class Utilisateur(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val nom: String = "",
    val prenom: String = "",
    val email: String = "",
    val motDePasse: String = "",
    val role: String = "ETUDIANT" // ETUDIANT, ENCADREUR, ADMIN
)