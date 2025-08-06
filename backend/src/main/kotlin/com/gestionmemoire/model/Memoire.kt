package com.gestionmemoire.model

import jakarta.persistence.*

@Entity
@Table(name = "memoire")
data class Memoire(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val titre: String = "",
    val description: String = "",
    val fichierUrl: String = "",
    @ManyToOne
    val etudiant: Utilisateur? = null,
    @ManyToOne
    val encadreur: Utilisateur? = null
)