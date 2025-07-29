package com.academique.memoires.service

import com.academique.memoires.entity.Soutenance
import com.academique.memoires.entity.StatutSoutenance
import com.academique.memoires.repository.SoutenanceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class SoutenanceService(
    private val soutenanceRepository: SoutenanceRepository,
    private val memoireService: MemoireService,
    private val juryService: JuryService
) {

    fun findAll(): List<Soutenance> = soutenanceRepository.findAll()

    fun findById(id: Long): Soutenance? = soutenanceRepository.findById(id).orElse(null)

    fun findByMemoireId(memoireId: Long): Soutenance? = soutenanceRepository.findByMemoireId(memoireId)

    fun findByJuryId(juryId: Long): List<Soutenance> = soutenanceRepository.findByJuryId(juryId)

    fun findByStatut(statut: StatutSoutenance): List<Soutenance> = soutenanceRepository.findByStatut(statut)

    fun findBySalle(salle: String): List<Soutenance> = soutenanceRepository.findBySalle(salle)

    fun findByDateSoutenanceBetween(dateDebut: LocalDateTime, dateFin: LocalDateTime): List<Soutenance> = 
        soutenanceRepository.findByDateSoutenanceBetween(dateDebut, dateFin)

    fun findByDateSoutenance(date: LocalDateTime): List<Soutenance> = 
        soutenanceRepository.findByDateSoutenance(date)

    fun findBySalleAndDate(salle: String, date: LocalDateTime): List<Soutenance> = 
        soutenanceRepository.findBySalleAndDate(salle, date)

    fun findByStatutOrderByDate(statut: StatutSoutenance): List<Soutenance> = 
        soutenanceRepository.findByStatutOrderByDateSoutenanceAsc(statut)

    fun countByJuryIdAndStatut(juryId: Long, statut: StatutSoutenance): Long = 
        soutenanceRepository.countByJuryIdAndStatut(juryId, statut)

    fun save(soutenance: Soutenance): Soutenance {
        validateSoutenance(soutenance)
        return soutenanceRepository.save(soutenance)
    }

    fun update(id: Long, soutenance: Soutenance): Soutenance? {
        return if (soutenanceRepository.existsById(id)) {
            validateSoutenance(soutenance)
            soutenanceRepository.save(soutenance.copy(id = id))
        } else {
            null
        }
    }

    fun updateStatut(id: Long, statut: StatutSoutenance): Soutenance? {
        val soutenance = findById(id)
        return if (soutenance != null) {
            soutenanceRepository.save(soutenance.copy(statut = statut))
        } else {
            null
        }
    }

    fun updateNote(id: Long, note: Double, mention: com.academique.memoires.entity.MentionSoutenance): Soutenance? {
        val soutenance = findById(id)
        return if (soutenance != null) {
            val updatedSoutenance = soutenance.copy(
                noteFinale = note,
                mention = mention,
                statut = StatutSoutenance.TERMINEE
            )
            soutenanceRepository.save(updatedSoutenance)
        } else {
            null
        }
    }

    fun deleteById(id: Long): Boolean {
        return if (soutenanceRepository.existsById(id)) {
            soutenanceRepository.deleteById(id)
            true
        } else {
            false
        }
    }

    fun isSlotAvailable(salle: String, date: LocalDateTime, dureeMinutes: Int, excludeId: Long? = null): Boolean {
        val soutenances = findBySalleAndDate(salle, date)
        val dateDebut = date
        val dateFin = date.plusMinutes(dureeMinutes.toLong())

        return soutenances.none { soutenance ->
            if (excludeId != null && soutenance.id == excludeId) {
                false
            } else {
                val soutenanceDebut = soutenance.dateSoutenance!!
                val soutenanceFin = soutenanceDebut.plusMinutes(soutenance.dureeMinutes.toLong())
                
                // Vérifier s'il y a chevauchement
                dateDebut < soutenanceFin && dateFin > soutenanceDebut
            }
        }
    }

    private fun validateSoutenance(soutenance: Soutenance) {
        // Vérifier que le mémoire existe
        if (soutenance.memoire?.id != null && memoireService.findById(soutenance.memoire.id) == null) {
            throw IllegalArgumentException("Le mémoire spécifié n'existe pas")
        }

        // Vérifier que le jury existe
        if (soutenance.jury?.id != null && juryService.findById(soutenance.jury.id) == null) {
            throw IllegalArgumentException("Le jury spécifié n'existe pas")
        }

        // Vérifier que la salle est disponible
        if (soutenance.dateSoutenance != null && soutenance.salle.isNotBlank()) {
            if (!isSlotAvailable(soutenance.salle, soutenance.dateSoutenance, soutenance.dureeMinutes, soutenance.id)) {
                throw IllegalArgumentException("La salle ${soutenance.salle} n'est pas disponible à cette date et heure")
            }
        }

        // Vérifier que la date de soutenance est dans le futur (pour les nouvelles soutenances)
        if (soutenance.id == 0L && soutenance.dateSoutenance != null && soutenance.dateSoutenance.isBefore(LocalDateTime.now())) {
            throw IllegalArgumentException("La date de soutenance doit être dans le futur")
        }
    }
}