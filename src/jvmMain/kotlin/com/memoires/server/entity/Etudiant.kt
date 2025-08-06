package com.memoires.server.entity

import jakarta.persistence.*
import jakarta.validation.constraints.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

/**
 * Entité JPA représentant un étudiant dans le système.
 * 
 * @Entity - Marque cette classe comme une entité JPA mappée à une table
 * @Table - Spécifie le nom de la table et les contraintes d'unicité
 * @EntityListeners - Active l'audit automatique des dates de création/modification
 */
@Entity
@Table(
    name = "etudiants",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["numero_etudiant"]),
        UniqueConstraint(columnNames = ["email"])
    ]
)
@EntityListeners(AuditingEntityListener::class)
data class Etudiant(
    /**
     * Identifiant unique généré automatiquement
     * @Id - Marque ce champ comme clé primaire
     * @GeneratedValue - Génération automatique de la valeur via une séquence
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    /**
     * Numéro unique de l'étudiant
     * @Column - Configuration de la colonne avec contraintes
     * @NotBlank - Validation : le champ ne doit pas être vide
     * @Size - Validation : longueur minimale et maximale
     */
    @Column(name = "numero_etudiant", nullable = false, unique = true, length = 20)
    @NotBlank(message = "Le numéro étudiant est obligatoire")
    @Size(min = 5, max = 20, message = "Le numéro étudiant doit contenir entre 5 et 20 caractères")
    val numeroEtudiant: String,

    /**
     * Nom de l'étudiant
     */
    @Column(nullable = false, length = 100)
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    val nom: String,

    /**
     * Prénom de l'étudiant
     */
    @Column(nullable = false, length = 100)
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères")
    val prenom: String,

    /**
     * Adresse email unique de l'étudiant
     * @Email - Validation du format email
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
     * Niveau d'étude (Licence, Master, Doctorat)
     * @Enumerated - Stockage de l'enum comme chaîne de caractères
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull(message = "Le niveau d'étude est obligatoire")
    val niveauEtude: NiveauEtude,

    /**
     * Filière d'étude
     */
    @Column(nullable = false, length = 100)
    @NotBlank(message = "La filière est obligatoire")
    @Size(max = 100, message = "La filière ne doit pas dépasser 100 caractères")
    val filiere: String,

    /**
     * Liste des mémoires de l'étudiant
     * @OneToMany - Relation un-à-plusieurs avec l'entité Memoire
     * mappedBy - Indique le champ dans l'entité Memoire qui fait référence à cette entité
     * cascade - Les opérations sur l'étudiant sont propagées aux mémoires
     * fetch - Chargement paresseux des mémoires
     */
    @OneToMany(mappedBy = "etudiant", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val memoires: MutableList<Memoire> = mutableListOf(),

    /**
     * Date de création automatique
     * @CreatedDate - Remplie automatiquement à la création
     */
    @CreatedDate
    @Column(name = "date_creation", nullable = false, updatable = false)
    val dateCreation: LocalDateTime? = null,

    /**
     * Date de dernière modification automatique
     * @LastModifiedDate - Mise à jour automatiquement à chaque modification
     */
    @LastModifiedDate
    @Column(name = "date_modification", nullable = false)
    val dateModification: LocalDateTime? = null
) {
    /**
     * Méthode pour obtenir le nom complet de l'étudiant
     */
    fun getNomComplet(): String = "$prenom $nom"
    
    /**
     * Override toString pour éviter les références circulaires avec les mémoires
     */
    override fun toString(): String {
        return "Etudiant(id=$id, numeroEtudiant='$numeroEtudiant', nom='$nom', prenom='$prenom', email='$email')"
    }
}

/**
 * Énumération des niveaux d'étude possibles
 */
enum class NiveauEtude(val libelle: String) {
    LICENCE("Licence"),
    MASTER("Master"),
    DOCTORAT("Doctorat")
}