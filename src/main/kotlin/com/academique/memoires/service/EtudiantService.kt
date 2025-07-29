package com.academique.memoires.service

import com.academique.memoires.entity.Etudiant
import com.academique.memoires.entity.NiveauEtude
import com.academique.memoires.repository.EtudiantRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class EtudiantService(
    private val etudiantRepository: EtudiantRepository
) {

    fun findAll(): List<Etudiant> = etudiantRepository.findAll()

    fun findById(id: Long): Etudiant? = etudiantRepository.findById(id).orElse(null)

    fun findByEmail(email: String): Etudiant? = etudiantRepository.findByEmail(email)

    fun findByNumeroEtudiant(numeroEtudiant: String): Etudiant? = etudiantRepository.findByNumeroEtudiant(numeroEtudiant)

    fun findByFiliere(filiere: String): List<Etudiant> = etudiantRepository.findByFiliere(filiere)

    fun findByNiveau(niveau: NiveauEtude): List<Etudiant> = etudiantRepository.findByNiveau(niveau)

    fun searchByName(nom: String): List<Etudiant> = etudiantRepository.findByNomOrPrenomContainingIgnoreCase(nom)

    fun save(etudiant: Etudiant): Etudiant {
        validateEtudiant(etudiant)
        return etudiantRepository.save(etudiant)
    }

    fun update(id: Long, etudiant: Etudiant): Etudiant? {
        return if (etudiantRepository.existsById(id)) {
            validateEtudiant(etudiant)
            etudiantRepository.save(etudiant.copy(id = id))
        } else {
            null
        }
    }

    fun deleteById(id: Long): Boolean {
        return if (etudiantRepository.existsById(id)) {
            etudiantRepository.deleteById(id)
            true
        } else {
            false
        }
    }

    fun existsByEmail(email: String): Boolean = etudiantRepository.existsByEmail(email)

    fun existsByNumeroEtudiant(numeroEtudiant: String): Boolean = etudiantRepository.existsByNumeroEtudiant(numeroEtudiant)

    private fun validateEtudiant(etudiant: Etudiant) {
        if (etudiant.id == 0L) {
            if (existsByEmail(etudiant.email)) {
                throw IllegalArgumentException("Un étudiant avec cet email existe déjà")
            }
            if (existsByNumeroEtudiant(etudiant.numeroEtudiant)) {
                throw IllegalArgumentException("Un étudiant avec ce numéro existe déjà")
            }
        }
    }
}