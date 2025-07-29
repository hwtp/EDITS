package com.academique.memoires.service

import com.academique.memoires.entity.Memoire
import com.academique.memoires.entity.StatutMemoire
import com.academique.memoires.entity.TypeMemoire
import com.academique.memoires.repository.MemoireRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional
class MemoireService(
    private val memoireRepository: MemoireRepository,
    private val etudiantService: EtudiantService,
    private val professeurService: ProfesseurService
) {

    fun findAll(): List<Memoire> = memoireRepository.findAll()

    fun findById(id: Long): Memoire? = memoireRepository.findById(id).orElse(null)

    fun findByEtudiantId(etudiantId: Long): List<Memoire> = memoireRepository.findByEtudiantId(etudiantId)

    fun findByDirecteurId(directeurId: Long): List<Memoire> = memoireRepository.findByDirecteurId(directeurId)

    fun findByCoDirecteurId(coDirecteurId: Long): List<Memoire> = memoireRepository.findByCoDirecteurId(coDirecteurId)

    fun findByStatut(statut: StatutMemoire): List<Memoire> = memoireRepository.findByStatut(statut)

    fun findByType(type: TypeMemoire): List<Memoire> = memoireRepository.findByType(type)

    fun searchByTitre(titre: String): List<Memoire> = memoireRepository.findByTitreContainingIgnoreCase(titre)

    fun searchByMotsCles(motCle: String): List<Memoire> = memoireRepository.findByMotsClesContainingIgnoreCase(motCle)

    fun findByDateDepotBetween(dateDebut: LocalDate, dateFin: LocalDate): List<Memoire> = 
        memoireRepository.findByDateDepotBetween(dateDebut, dateFin)

    fun findByStatutAndType(statut: StatutMemoire, type: TypeMemoire): List<Memoire> = 
        memoireRepository.findByStatutAndType(statut, type)

    fun countByDirecteurIdAndStatut(directeurId: Long, statut: StatutMemoire): Long = 
        memoireRepository.countByDirecteurIdAndStatut(directeurId, statut)

    fun save(memoire: Memoire): Memoire {
        validateMemoire(memoire)
        return memoireRepository.save(memoire)
    }

    fun update(id: Long, memoire: Memoire): Memoire? {
        return if (memoireRepository.existsById(id)) {
            validateMemoire(memoire)
            memoireRepository.save(memoire.copy(id = id))
        } else {
            null
        }
    }

    fun updateStatut(id: Long, statut: StatutMemoire): Memoire? {
        val memoire = findById(id)
        return if (memoire != null) {
            val updatedMemoire = memoire.copy(statut = statut)
            if (statut == StatutMemoire.DEPOSE && memoire.dateDepot == null) {
                memoireRepository.save(updatedMemoire.copy(dateDepot = LocalDate.now()))
            } else {
                memoireRepository.save(updatedMemoire)
            }
        } else {
            null
        }
    }

    fun deleteById(id: Long): Boolean {
        return if (memoireRepository.existsById(id)) {
            memoireRepository.deleteById(id)
            true
        } else {
            false
        }
    }

    private fun validateMemoire(memoire: Memoire) {
        // Vérifier que l'étudiant existe
        if (memoire.etudiant?.id != null && etudiantService.findById(memoire.etudiant.id) == null) {
            throw IllegalArgumentException("L'étudiant spécifié n'existe pas")
        }

        // Vérifier que le directeur existe
        if (memoire.directeur?.id != null && professeurService.findById(memoire.directeur.id) == null) {
            throw IllegalArgumentException("Le directeur spécifié n'existe pas")
        }

        // Vérifier que le co-directeur existe (s'il est spécifié)
        if (memoire.coDirecteur?.id != null && professeurService.findById(memoire.coDirecteur.id) == null) {
            throw IllegalArgumentException("Le co-directeur spécifié n'existe pas")
        }

        // Vérifier que le directeur et co-directeur sont différents
        if (memoire.directeur?.id != null && memoire.coDirecteur?.id != null && 
            memoire.directeur.id == memoire.coDirecteur.id) {
            throw IllegalArgumentException("Le directeur et le co-directeur doivent être différents")
        }
    }
}