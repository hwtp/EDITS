package com.academique.memoires.entity

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

@Entity
@Table(name = "professeurs")
data class Professeur(
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

    @Column(nullable = false)
    @NotBlank(message = "Le département est obligatoire")
    val departement: String = "",

    @Column(nullable = false)
    @NotBlank(message = "La spécialité est obligatoire")
    val specialite: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val grade: GradeProfesseur = GradeProfesseur.ASSISTANT,

    @OneToMany(mappedBy = "directeur", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val memoiresDirecteur: List<Memoire> = emptyList(),

    @OneToMany(mappedBy = "coDirecteur", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val memoiresCoDirecteur: List<Memoire> = emptyList(),

    @ManyToMany(mappedBy = "membres")
    val jurys: List<Jury> = emptyList()
)

enum class GradeProfesseur {
    ASSISTANT, MAITRE_ASSISTANT, MAITRE_CONFERENCE, PROFESSEUR
}