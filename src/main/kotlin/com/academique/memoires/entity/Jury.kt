package com.academique.memoires.entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "jurys")
data class Jury(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "date_creation")
    val dateCreation: LocalDate = LocalDate.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "president_id", nullable = false)
    val president: Professeur? = null,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "jury_membres",
        joinColumns = [JoinColumn(name = "jury_id")],
        inverseJoinColumns = [JoinColumn(name = "professeur_id")]
    )
    val membres: List<Professeur> = emptyList(),

    @OneToMany(mappedBy = "jury", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val soutenances: List<Soutenance> = emptyList()
)