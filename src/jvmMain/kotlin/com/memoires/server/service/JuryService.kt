package com.memoires.server.service

import com.memoires.server.entity.Jury
import com.memoires.server.entity.StatutJury
import com.memoires.server.repository.JuryRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

/**
 * Service pour la gestion des jurys de soutenance.
 */
@Service
@Transactional
class JuryService(
    private val juryRepository: JuryRepository
) {
    
    /**
     * Récupère tous les jurys avec pagination.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<Jury> {
        return juryRepository.findAll(pageable)
    }
    
    /**
     * Récupère un jury par son ID.
     */
    @Transactional(readOnly = true)
    fun findById(id: Long): Optional<Jury> {
        return juryRepository.findById(id)
    }
    
    /**
     * Sauvegarde un jury.
     */
    fun save(jury: Jury): Jury {
        return juryRepository.save(jury)
    }
    
    /**
     * Supprime un jury par son ID.
     */
    fun deleteById(id: Long) {
        juryRepository.deleteById(id)
    }
    
    /**
     * Recherche des jurys par statut.
     */
    @Transactional(readOnly = true)
    fun findByStatut(statut: StatutJury, pageable: Pageable): Page<Jury> {
        return juryRepository.findByStatut(statut, pageable)
    }
    
    /**
     * Recherche un jury par l'ID du mémoire.
     */
    @Transactional(readOnly = true)
    fun findByMemoireId(memoireId: Long): Optional<Jury> {
        return juryRepository.findByMemoireId(memoireId)
    }
    
    /**
     * Recherche des jurys dans une période donnée.
     */
    @Transactional(readOnly = true)
    fun findByDateHeureSoutenanceBetween(
        dateDebut: LocalDateTime, 
        dateFin: LocalDateTime, 
        pageable: Pageable
    ): Page<Jury> {
        return juryRepository.findByDateHeureSoutenanceBetween(dateDebut, dateFin, pageable)
    }
    
    /**
     * Trouve les jurys d'aujourd'hui.
     */
    @Transactional(readOnly = true)
    fun findJurysAujourdhui(
        dateDebut: LocalDateTime,
        dateFin: LocalDateTime,
        pageable: Pageable
    ): Page<Jury> {
        return juryRepository.findJurysAujourdhui(dateDebut, dateFin, pageable)
    }
    
    /**
     * Compte le nombre total de jurys.
     */
    @Transactional(readOnly = true)
    fun count(): Long {
        return juryRepository.count()
    }
    
    /**
     * Compte les jurys par statut.
     */
    @Transactional(readOnly = true)
    fun countByStatut(statut: StatutJury): Long {
        return juryRepository.countByStatut(statut)
    }
}