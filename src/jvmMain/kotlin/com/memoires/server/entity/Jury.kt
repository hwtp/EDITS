package com.memoires.server.entity

import jakarta.persistence.*
import jakarta.validation.constraints.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

/**
 * Entité JPA représentant un jury de soutenance.
 * Un jury est composé de plusieurs enseignants et est associé à un mémoire spécifique.
 */
@Entity
@Table(name = "jurys")
@EntityListeners(AuditingEntityListener::class)
data class Jury(
    /**
     * Identifiant unique généré automatiquement
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    /**
     * Date et heure de la soutenance
     */
    @Column(name = "date_heure_soutenance", nullable = false)
    @NotNull(message = "La date et heure de soutenance sont obligatoires")
    val dateHeureSoutenance: LocalDateTime,

    /**
     * Lieu de la soutenance (salle, amphithéâtre, etc.)
     */
    @Column(nullable = false, length = 200)
    @NotBlank(message = "Le lieu est obligatoire")
    @Size(max = 200, message = "Le lieu ne doit pas dépasser 200 caractères")
    val lieu: String,

    /**
     * Durée prévue de la soutenance en minutes
     */
    @Column(name = "duree_prevue")
    @Min(value = 30, message = "La durée doit être d'au moins 30 minutes")
    @Max(value = 480, message = "La durée ne peut pas dépasser 8 heures")
    val dureePrevue: Int = 60,

    /**
     * Statut du jury
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull(message = "Le statut est obligatoire")
    val statut: StatutJury = StatutJury.PLANIFIE,

    /**
     * Mémoire associé à ce jury
     * @OneToOne - Relation un-à-un avec l'entité Memoire
     * @JoinColumn - Spécifie la colonne de jointure
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memoire_id", nullable = false)
    @NotNull(message = "Le mémoire est obligatoire")
    val memoire: Memoire,

    /**
     * Président du jury
     * @ManyToOne - Relation plusieurs-à-un avec l'entité Enseignant
     * Le président doit avoir un grade élevé (Professeur ou Maître de conférences)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "president_id", nullable = false)
    @NotNull(message = "Le président du jury est obligatoire")
    val president: Enseignant,

    /**
     * Membres du jury (incluant le président et les examinateurs)
     * @ManyToMany - Relation plusieurs-à-plusieurs avec l'entité Enseignant
     * @JoinTable - Définit la table de liaison entre Jury et Enseignant
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "jury_membres",
        joinColumns = [JoinColumn(name = "jury_id")],
        inverseJoinColumns = [JoinColumn(name = "enseignant_id")]
    )
    val membres: MutableList<Enseignant> = mutableListOf(),

    /**
     * Rapporteur du jury (membre qui présente le rapport d'évaluation)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rapporteur_id")
    val rapporteur: Enseignant? = null,

    /**
     * Procès-verbal de la soutenance
     */
    @Column(name = "proces_verbal", columnDefinition = "TEXT")
    @Size(max = 5000, message = "Le procès-verbal ne doit pas dépasser 5000 caractères")
    val procesVerbal: String? = null,

    /**
     * Observations du jury
     */
    @Column(columnDefinition = "TEXT")
    @Size(max = 2000, message = "Les observations ne doivent pas dépasser 2000 caractères")
    val observations: String? = null,

    /**
     * Recommandations du jury
     */
    @Column(columnDefinition = "TEXT")
    @Size(max = 2000, message = "Les recommandations ne doivent pas dépasser 2000 caractères")
    val recommandations: String? = null,

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
     * Méthode pour vérifier si le jury est complet
     * Un jury doit avoir au minimum 3 membres pour être valide
     */
    fun estComplet(): Boolean {
        return membres.size >= 3 && president != null
    }
    
    /**
     * Méthode pour vérifier si la soutenance peut avoir lieu
     */
    fun peutAvoirLieu(): Boolean {
        return statut == StatutJury.PLANIFIE && estComplet() && 
               dateHeureSoutenance.isAfter(LocalDateTime.now())
    }
    
    /**
     * Méthode pour obtenir le nombre de membres du jury
     */
    fun getNombreMembres(): Int = membres.size
    
    /**
     * Override toString pour éviter les références circulaires
     */
    override fun toString(): String {
        return "Jury(id=$id, dateHeureSoutenance=$dateHeureSoutenance, lieu='$lieu', statut=$statut)"
    }
}

/**
 * Énumération des statuts possibles d'un jury
 */
enum class StatutJury(val libelle: String) {
    PLANIFIE("Planifié"),
    CONFIRME("Confirmé"),
    EN_COURS("En cours"),
    TERMINE("Terminé"),
    REPORTE("Reporté"),
    ANNULE("Annulé")
}