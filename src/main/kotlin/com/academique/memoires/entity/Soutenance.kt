package com.academique.memoires.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

@Entity
@Table(name = "soutenances")
data class Soutenance(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "date_soutenance", nullable = false)
    val dateSoutenance: LocalDateTime? = null,

    @Column(nullable = false)
    @NotBlank(message = "La salle est obligatoire")
    val salle: String = "",

    @Column(name = "duree_minutes")
    val dureeMinutes: Int = 60,

    @Column(columnDefinition = "TEXT")
    val observations: String = "",

    @Column(name = "note_finale")
    val noteFinale: Double? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val statut: StatutSoutenance = StatutSoutenance.PROGRAMMEE,

    @Enumerated(EnumType.STRING)
    val mention: MentionSoutenance? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memoire_id", nullable = false)
    val memoire: Memoire? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jury_id", nullable = false)
    val jury: Jury? = null
)

enum class StatutSoutenance {
    PROGRAMMEE, EN_COURS, TERMINEE, REPORTEE, ANNULEE
}

enum class MentionSoutenance {
    PASSABLE, ASSEZ_BIEN, BIEN, TRES_BIEN, EXCELLENT
}