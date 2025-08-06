package com.memoires.server.service

import com.memoires.server.entity.Memoire
import com.memoires.server.entity.StatutMemoire
import com.memoires.server.entity.TypeMemoire
import com.memoires.server.repository.MemoireRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

/**
 * Service pour la gestion des mémoires de soutenance.
 */
@Service
@Transactional
class MemoireService(
    private val memoireRepository: MemoireRepository
) {
    
    /**
     * Récupère tous les mémoires avec pagination.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<Memoire> {
        return memoireRepository.findAll(pageable)
    }
    
    /**
     * Récupère un mémoire par son ID.
     */
    @Transactional(readOnly = true)
    fun findById(id: Long): Optional<Memoire> {
        return memoireRepository.findById(id)
    }
    
    /**
     * Sauvegarde un mémoire.
     */
    fun save(memoire: Memoire): Memoire {
        return memoireRepository.save(memoire)
    }
    
    /**
     * Supprime un mémoire par son ID.
     */
    fun deleteById(id: Long) {
        memoireRepository.deleteById(id)
    }
    
    /**
     * Recherche des mémoires par statut.
     */
    @Transactional(readOnly = true)
    fun findByStatut(statut: StatutMemoire, pageable: Pageable): Page<Memoire> {
        return memoireRepository.findByStatut(statut, pageable)
    }
    
    /**
     * Recherche des mémoires par type.
     */
    @Transactional(readOnly = true)
    fun findByTypeMemoire(typeMemoire: TypeMemoire, pageable: Pageable): Page<Memoire> {
        return memoireRepository.findByTypeMemoire(typeMemoire, pageable)
    }
    
    /**
     * Recherche globale de mémoires.
     */
    @Transactional(readOnly = true)
    fun searchMemoires(searchTerm: String, pageable: Pageable): Page<Memoire> {
        return memoireRepository.searchMemoires(searchTerm.trim(), pageable)
    }
    
    /**
     * Trouve les mémoires prêts à être planifiés pour soutenance.
     */
    @Transactional(readOnly = true)
    fun findMemoiresPretsAPlanifier(pageable: Pageable): Page<Memoire> {
        return memoireRepository.findMemoiresPretsAPlanifier(pageable)
    }
    
    /**
     * Trouve les prochaines soutenances.
     */
    @Transactional(readOnly = true)
    fun findProchainesSoutenances(dateLimite: LocalDateTime, pageable: Pageable): Page<Memoire> {
        return memoireRepository.findProchainesSoutenances(dateLimite, pageable)
    }
    
    /**
     * Compte le nombre total de mémoires.
     */
    @Transactional(readOnly = true)
    fun count(): Long {
        return memoireRepository.count()
    }
    
    /**
     * Compte les mémoires par statut.
     */
    @Transactional(readOnly = true)
    fun countByStatut(statut: StatutMemoire): Long {
        return memoireRepository.countByStatut(statut)
    }
    
    /**
     * Compte les mémoires par type.
     */
    @Transactional(readOnly = true)
    fun countByTypeMemoire(typeMemoire: TypeMemoire): Long {
        return memoireRepository.countByTypeMemoire(typeMemoire)
    }
}