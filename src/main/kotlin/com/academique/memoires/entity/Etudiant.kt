package com.academique.memoires.entity

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import java.time.LocalDate

@Entity
@Table(name = "etudiants")
data class Etudiant(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    @NotBlank(message = "Le nom est obligatoire")
    val nom: String = "",

    @Column(nullable = false)
    @NotBlank(message = "Le prénom est obligatoire")
    val prenom: String = "",

    @Column(unique = true, nullable = false)
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    val email: String = "",

    @Column(name = "numero_etudiant", unique = true, nullable = false)
    @NotBlank(message = "Le numéro étudiant est obligatoire")
    val numeroEtudiant: String = "",

    @Column(name = "date_naissance")
    val dateNaissance: LocalDate? = null,

    @Column(nullable = false)
    @NotBlank(message = "La filière est obligatoire")
    val filiere: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val niveau: NiveauEtude = NiveauEtude.LICENCE,

    @OneToMany(mappedBy = "etudiant", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val memoires: List<Memoire> = emptyList()
)

enum class NiveauEtude {
    LICENCE, MASTER, DOCTORAT
}