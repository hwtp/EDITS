package com.academique.memoires.service

import com.academique.memoires.entity.GradeProfesseur
import com.academique.memoires.entity.Professeur
import com.academique.memoires.repository.ProfesseurRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProfesseurService(
    private val professeurRepository: ProfesseurRepository
) {

    fun findAll(): List<Professeur> = professeurRepository.findAll()

    fun findById(id: Long): Professeur? = professeurRepository.findById(id).orElse(null)

    fun findByEmail(email: String): Professeur? = professeurRepository.findByEmail(email)

    fun findByDepartement(departement: String): List<Professeur> = professeurRepository.findByDepartement(departement)

    fun findBySpecialite(specialite: String): List<Professeur> = professeurRepository.findBySpecialite(specialite)

    fun findByGrade(grade: GradeProfesseur): List<Professeur> = professeurRepository.findByGrade(grade)

    fun searchByName(nom: String): List<Professeur> = professeurRepository.findByNomOrPrenomContainingIgnoreCase(nom)

    fun findByDepartementAndSpecialite(departement: String, specialite: String): List<Professeur> = 
        professeurRepository.findByDepartementAndSpecialite(departement, specialite)

    fun save(professeur: Professeur): Professeur {
        validateProfesseur(professeur)
        return professeurRepository.save(professeur)
    }

    fun update(id: Long, professeur: Professeur): Professeur? {
        return if (professeurRepository.existsById(id)) {
            validateProfesseur(professeur)
            professeurRepository.save(professeur.copy(id = id))
        } else {
            null
        }
    }

    fun deleteById(id: Long): Boolean {
        return if (professeurRepository.existsById(id)) {
            professeurRepository.deleteById(id)
            true
        } else {
            false
        }
    }

    fun existsByEmail(email: String): Boolean = professeurRepository.existsByEmail(email)

    private fun validateProfesseur(professeur: Professeur) {
        if (professeur.id == 0L && existsByEmail(professeur.email)) {
            throw IllegalArgumentException("Un professeur avec cet email existe déjà")
        }
    }
}