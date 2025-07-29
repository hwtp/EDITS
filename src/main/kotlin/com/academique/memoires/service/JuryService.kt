package com.academique.memoires.service

import com.academique.memoires.entity.Jury
import com.academique.memoires.repository.JuryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional
class JuryService(
    private val juryRepository: JuryRepository,
    private val professeurService: ProfesseurService
) {

    fun findAll(): List<Jury> = juryRepository.findAll()

    fun findById(id: Long): Jury? = juryRepository.findById(id).orElse(null)

    fun findByPresidentId(presidentId: Long): List<Jury> = juryRepository.findByPresidentId(presidentId)

    fun findByMembreId(membreId: Long): List<Jury> = juryRepository.findByMembreId(membreId)

    fun findByDateCreationBetween(dateDebut: LocalDate, dateFin: LocalDate): List<Jury> = 
        juryRepository.findByDateCreationBetween(dateDebut, dateFin)

    fun countByPresidentId(presidentId: Long): Long = juryRepository.countByPresidentId(presidentId)

    fun save(jury: Jury): Jury {
        validateJury(jury)
        return juryRepository.save(jury)
    }

    fun update(id: Long, jury: Jury): Jury? {
        return if (juryRepository.existsById(id)) {
            validateJury(jury)
            juryRepository.save(jury.copy(id = id))
        } else {
            null
        }
    }

    fun deleteById(id: Long): Boolean {
        return if (juryRepository.existsById(id)) {
            juryRepository.deleteById(id)
            true
        } else {
            false
        }
    }

    private fun validateJury(jury: Jury) {
        // Vérifier que le président existe
        if (jury.president?.id != null && professeurService.findById(jury.president.id) == null) {
            throw IllegalArgumentException("Le président du jury spécifié n'existe pas")
        }

        // Vérifier que tous les membres existent
        jury.membres.forEach { membre ->
            if (professeurService.findById(membre.id) == null) {
                throw IllegalArgumentException("Le membre du jury ${membre.nom} ${membre.prenom} n'existe pas")
            }
        }

        // Vérifier que le président n'est pas aussi membre
        if (jury.president != null && jury.membres.any { it.id == jury.president.id }) {
            throw IllegalArgumentException("Le président ne peut pas être aussi membre du jury")
        }

        // Vérifier qu'il y a au moins 3 membres (président + 2 membres minimum)
        if (jury.membres.size < 2) {
            throw IllegalArgumentException("Un jury doit avoir au minimum 2 membres en plus du président")
        }

        // Vérifier qu'il n'y a pas de doublons dans les membres
        val membresIds = jury.membres.map { it.id }
        if (membresIds.size != membresIds.distinct().size) {
            throw IllegalArgumentException("Un professeur ne peut pas être membre du jury plusieurs fois")
        }
    }
}