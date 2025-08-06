package com.memoires.server.entity

import jakarta.persistence.*
import jakarta.validation.constraints.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

/**
 * Entité JPA représentant un mémoire de soutenance.
 * Cette entité centrale lie un étudiant, un directeur de mémoire et éventuellement un jury.
 */
@Entity
@Table(name = "memoires")
@EntityListeners(AuditingEntityListener::class)
data class Memoire(
    /**
     * Identifiant unique généré automatiquement
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    /**
     * Titre du mémoire
     */
    @Column(nullable = false, length = 500)
    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 500, message = "Le titre ne doit pas dépasser 500 caractères")
    val titre: String,

    /**
     * Résumé/description du mémoire
     */
    @Column(columnDefinition = "TEXT")
    @Size(max = 5000, message = "Le résumé ne doit pas dépasser 5000 caractères")
    val resume: String? = null,

    /**
     * Mots-clés du mémoire (séparés par des virgules)
     */
    @Column(name = "mots_cles", length = 500)
    @Size(max = 500, message = "Les mots-clés ne doivent pas dépasser 500 caractères")
    val motsCles: String? = null,

    /**
     * Statut actuel du mémoire
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @NotNull(message = "Le statut est obligatoire")
    val statut: StatutMemoire = StatutMemoire.EN_COURS,

    /**
     * Type de mémoire (Licence, Master, Thèse)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type_memoire", nullable = false, length = 20)
    @NotNull(message = "Le type de mémoire est obligatoire")
    val typeMemoire: TypeMemoire,

    /**
     * Année académique du mémoire
     */
    @Column(name = "annee_academique", nullable = false, length = 9)
    @NotBlank(message = "L'année académique est obligatoire")
    @Pattern(regexp = "\\d{4}-\\d{4}", message = "Format d'année académique invalide (ex: 2023-2024)")
    val anneeAcademique: String,

    /**
     * Date de dépôt du mémoire
     */
    @Column(name = "date_depot")
    val dateDepot: LocalDateTime? = null,

    /**
     * Date prévue de soutenance
     */
    @Column(name = "date_soutenance_prevue")
    val dateSoutenancePrevue: LocalDateTime? = null,

    /**
     * Date réelle de soutenance
     */
    @Column(name = "date_soutenance_reelle")
    val dateSoutenanceReelle: LocalDateTime? = null,

    /**
     * Note obtenue lors de la soutenance
     */
    @Column(precision = 4, scale = 2)
    @DecimalMin(value = "0.0", message = "La note doit être positive")
    @DecimalMax(value = "20.0", message = "La note ne peut pas dépasser 20")
    val note: Double? = null,

    /**
     * Mention obtenue
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    val mention: MentionMemoire? = null,

    /**
     * Chemin vers le fichier PDF du mémoire
     */
    @Column(name = "chemin_fichier", length = 500)
    @Size(max = 500, message = "Le chemin du fichier ne doit pas dépasser 500 caractères")
    val cheminFichier: String? = null,

    /**
     * Nom original du fichier uploadé
     */
    @Column(name = "nom_fichier_original", length = 255)
    @Size(max = 255, message = "Le nom du fichier ne doit pas dépasser 255 caractères")
    val nomFichierOriginal: String? = null,

    /**
     * Taille du fichier en octets
     */
    @Column(name = "taille_fichier")
    val tailleFichier: Long? = null,

    /**
     * Étudiant auteur du mémoire
     * @ManyToOne - Relation plusieurs-à-un avec l'entité Etudiant
     * @JoinColumn - Spécifie la colonne de jointure dans la table
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    @NotNull(message = "L'étudiant est obligatoire")
    val etudiant: Etudiant,

    /**
     * Directeur du mémoire
     * @ManyToOne - Relation plusieurs-à-un avec l'entité Enseignant
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "directeur_id", nullable = false)
    @NotNull(message = "Le directeur est obligatoire")
    val directeur: Enseignant,

    /**
     * Co-directeur du mémoire (optionnel)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "co_directeur_id")
    val coDirecteur: Enseignant? = null,

    /**
     * Jury de soutenance (optionnel, créé lors de la planification de la soutenance)
     * @OneToOne - Relation un-à-un avec l'entité Jury
     */
    @OneToOne(mappedBy = "memoire", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val jury: Jury? = null,

    /**
     * Observations et commentaires
     */
    @Column(columnDefinition = "TEXT")
    @Size(max = 2000, message = "Les observations ne doivent pas dépasser 2000 caractères")
    val observations: String? = null,

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
     * Méthode pour vérifier si le mémoire peut être soutenu
     */
    fun peutEtreSoutenu(): Boolean {
        return statut == StatutMemoire.DEPOSE && cheminFichier != null
    }
    
    /**
     * Méthode pour vérifier si le mémoire a été soutenu
     */
    fun estSoutenu(): Boolean {
        return statut == StatutMemoire.SOUTENU && dateSoutenanceReelle != null
    }
    
    /**
     * Override toString pour éviter les références circulaires
     */
    override fun toString(): String {
        return "Memoire(id=$id, titre='$titre', statut=$statut, typeMemoire=$typeMemoire)"
    }
}

/**
 * Énumération des statuts possibles d'un mémoire
 */
enum class StatutMemoire(val libelle: String) {
    EN_COURS("En cours"),
    DEPOSE("Déposé"),
    VALIDE("Validé"),
    PLANIFIE("Planifié"),
    SOUTENU("Soutenu"),
    REJETE("Rejeté"),
    REPORTE("Reporté")
}

/**
 * Énumération des types de mémoire
 */
enum class TypeMemoire(val libelle: String) {
    LICENCE("Mémoire de Licence"),
    MASTER("Mémoire de Master"),
    THESE("Thèse de Doctorat")
}

/**
 * Énumération des mentions possibles
 */
enum class MentionMemoire(val libelle: String, val noteMin: Double, val noteMax: Double) {
    PASSABLE("Passable", 10.0, 11.99),
    ASSEZ_BIEN("Assez Bien", 12.0, 13.99),
    BIEN("Bien", 14.0, 15.99),
    TRES_BIEN("Très Bien", 16.0, 17.99),
    EXCELLENT("Excellent", 18.0, 20.0);
    
    companion object {
        /**
         * Détermine la mention en fonction de la note
         */
        fun fromNote(note: Double): MentionMemoire? {
            return values().find { note >= it.noteMin && note <= it.noteMax }
        }
    }
}