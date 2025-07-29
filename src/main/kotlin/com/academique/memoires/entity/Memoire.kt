package com.academique.memoires.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import java.time.LocalDate

@Entity
@Table(name = "memoires")
data class Memoire(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    @NotBlank(message = "Le titre est obligatoire")
    val titre: String = "",

    @Column(columnDefinition = "TEXT")
    val resume: String = "",

    @Column(name = "mots_cles")
    val motsCles: String = "",

    @Column(name = "date_depot")
    val dateDepot: LocalDate? = null,

    @Column(name = "date_creation")
    val dateCreation: LocalDate = LocalDate.now(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val statut: StatutMemoire = StatutMemoire.EN_COURS,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: TypeMemoire = TypeMemoire.LICENCE,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    val etudiant: Etudiant? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "directeur_id", nullable = false)
    val directeur: Professeur? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "co_directeur_id")
    val coDirecteur: Professeur? = null,

    @OneToOne(mappedBy = "memoire", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val soutenance: Soutenance? = null
)

enum class StatutMemoire {
    EN_COURS, DEPOSE, VALIDE, REJETE, SOUTENU
}

enum class TypeMemoire {
    LICENCE, MASTER, DOCTORAT
}