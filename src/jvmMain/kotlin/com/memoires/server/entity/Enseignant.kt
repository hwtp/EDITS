package com.memoires.server.entity

import jakarta.persistence.*
import jakarta.validation.constraints.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

/**
 * Entité JPA représentant un enseignant dans le système.
 * Les enseignants peuvent être directeurs de mémoire ou membres de jury.
 */
@Entity
@Table(
    name = "enseignants",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["email"])
    ]
)
@EntityListeners(AuditingEntityListener::class)
data class Enseignant(
    /**
     * Identifiant unique généré automatiquement
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    /**
     * Nom de l'enseignant
     */
    @Column(nullable = false, length = 100)
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    val nom: String,

    /**
     * Prénom de l'enseignant
     */
    @Column(nullable = false, length = 100)
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères")
    val prenom: String,

    /**
     * Adresse email unique de l'enseignant
     */
    @Column(nullable = false, unique = true, length = 255)
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    @Size(max = 255, message = "L'email ne doit pas dépasser 255 caractères")
    val email: String,

    /**
     * Numéro de téléphone
     */
    @Column(name = "telephone", length = 20)
    @Size(max = 20, message = "Le téléphone ne doit pas dépasser 20 caractères")
    val telephone: String? = null,

    /**
     * Grade de l'enseignant (Professeur, Maître de conférences, etc.)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @NotNull(message = "Le grade est obligatoire")
    val grade: GradeEnseignant,

    /**
     * Spécialité/domaine d'expertise
     */
    @Column(nullable = false, length = 200)
    @NotBlank(message = "La spécialité est obligatoire")
    @Size(max = 200, message = "La spécialité ne doit pas dépasser 200 caractères")
    val specialite: String,

    /**
     * Département d'appartenance
     */
    @Column(nullable = false, length = 100)
    @NotBlank(message = "Le département est obligatoire")
    @Size(max = 100, message = "Le département ne doit pas dépasser 100 caractères")
    val departement: String,

    /**
     * Liste des mémoires dirigés par cet enseignant
     * @OneToMany - Relation un-à-plusieurs avec l'entité Memoire
     */
    @OneToMany(mappedBy = "directeur", fetch = FetchType.LAZY)
    val memoiresDiriges: MutableList<Memoire> = mutableListOf(),

    /**
     * Liste des jurys auxquels cet enseignant participe
     * @ManyToMany - Relation plusieurs-à-plusieurs avec l'entité Jury
     */
    @ManyToMany(mappedBy = "membres", fetch = FetchType.LAZY)
    val jurys: MutableList<Jury> = mutableListOf(),

    /**
     * Date de création automatique
     */
    @CreatedDate
    @Column(name = "date_creation", nullable = false, updatable = false)
    val dateCreation: LocalDateTime? = null,

    /**
     * Date de dernière modification automatique
     */
    @LastModifiedDate
    @Column(name = "date_modification", nullable = false)
    val dateModification: LocalDateTime? = null
) {
    /**
     * Méthode pour obtenir le nom complet avec le grade
     */
    fun getNomCompletAvecGrade(): String = "${grade.libelle} $prenom $nom"
    
    /**
     * Méthode pour obtenir le nom complet simple
     */
    fun getNomComplet(): String = "$prenom $nom"
    
    /**
     * Override toString pour éviter les références circulaires
     */
    override fun toString(): String {
        return "Enseignant(id=$id, nom='$nom', prenom='$prenom', email='$email', grade=$grade)"
    }
}

/**
 * Énumération des grades d'enseignant possibles
 */
enum class GradeEnseignant(val libelle: String) {
    PROFESSEUR("Professeur"),
    MAITRE_CONFERENCES("Maître de conférences"),
    MAITRE_ASSISTANT("Maître Assistant"),
    ASSISTANT("Assistant"),
    CHARGE_COURS("Chargé de cours"),
    VACATAIRE("Vacataire")
}