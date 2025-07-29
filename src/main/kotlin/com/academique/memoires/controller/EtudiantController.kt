package com.academique.memoires.controller

import com.academique.memoires.entity.Etudiant
import com.academique.memoires.entity.NiveauEtude
import com.academique.memoires.service.EtudiantService
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/etudiants")
class EtudiantController(
    private val etudiantService: EtudiantService
) {

    @GetMapping
    fun list(@RequestParam(required = false) search: String?, model: Model): String {
        val etudiants = if (search.isNullOrBlank()) {
            etudiantService.findAll()
        } else {
            etudiantService.searchByName(search)
        }
        
        model.addAttribute("etudiants", etudiants)
        model.addAttribute("search", search ?: "")
        return "etudiants/list"
    }

    @GetMapping("/new")
    fun showCreateForm(model: Model): String {
        model.addAttribute("etudiant", Etudiant())
        model.addAttribute("niveaux", NiveauEtude.values())
        return "etudiants/form"
    }

    @PostMapping
    fun create(@Valid @ModelAttribute etudiant: Etudiant, 
               bindingResult: BindingResult,
               redirectAttributes: RedirectAttributes): String {
        if (bindingResult.hasErrors()) {
            return "etudiants/form"
        }

        try {
            etudiantService.save(etudiant)
            redirectAttributes.addFlashAttribute("successMessage", "Étudiant créé avec succès")
        } catch (e: IllegalArgumentException) {
            bindingResult.rejectValue("email", "error.etudiant", e.message)
            return "etudiants/form"
        }

        return "redirect:/etudiants"
    }

    @GetMapping("/{id}")
    fun show(@PathVariable id: Long, model: Model): String {
        val etudiant = etudiantService.findById(id)
            ?: return "redirect:/etudiants"
        
        model.addAttribute("etudiant", etudiant)
        return "etudiants/show"
    }

    @GetMapping("/{id}/edit")
    fun showEditForm(@PathVariable id: Long, model: Model): String {
        val etudiant = etudiantService.findById(id)
            ?: return "redirect:/etudiants"
        
        model.addAttribute("etudiant", etudiant)
        model.addAttribute("niveaux", NiveauEtude.values())
        return "etudiants/form"
    }

    @PostMapping("/{id}")
    fun update(@PathVariable id: Long,
               @Valid @ModelAttribute etudiant: Etudiant,
               bindingResult: BindingResult,
               redirectAttributes: RedirectAttributes): String {
        if (bindingResult.hasErrors()) {
            return "etudiants/form"
        }

        try {
            etudiantService.update(id, etudiant)
            redirectAttributes.addFlashAttribute("successMessage", "Étudiant modifié avec succès")
        } catch (e: IllegalArgumentException) {
            bindingResult.rejectValue("email", "error.etudiant", e.message)
            return "etudiants/form"
        }

        return "redirect:/etudiants"
    }

    @PostMapping("/{id}/delete")
    fun delete(@PathVariable id: Long, redirectAttributes: RedirectAttributes): String {
        if (etudiantService.deleteById(id)) {
            redirectAttributes.addFlashAttribute("successMessage", "Étudiant supprimé avec succès")
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression")
        }
        return "redirect:/etudiants"
    }
}