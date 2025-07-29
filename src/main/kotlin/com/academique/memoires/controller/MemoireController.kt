package com.academique.memoires.controller

import com.academique.memoires.entity.Memoire
import com.academique.memoires.entity.StatutMemoire
import com.academique.memoires.entity.TypeMemoire
import com.academique.memoires.service.EtudiantService
import com.academique.memoires.service.MemoireService
import com.academique.memoires.service.ProfesseurService
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/memoires")
class MemoireController(
    private val memoireService: MemoireService,
    private val etudiantService: EtudiantService,
    private val professeurService: ProfesseurService
) {

    @GetMapping
    fun list(@RequestParam(required = false) search: String?,
             @RequestParam(required = false) statut: StatutMemoire?,
             @RequestParam(required = false) type: TypeMemoire?,
             model: Model): String {
        
        val memoires = when {
            !search.isNullOrBlank() -> memoireService.searchByTitre(search)
            statut != null && type != null -> memoireService.findByStatutAndType(statut, type)
            statut != null -> memoireService.findByStatut(statut)
            type != null -> memoireService.findByType(type)
            else -> memoireService.findAll()
        }
        
        model.addAttribute("memoires", memoires)
        model.addAttribute("search", search ?: "")
        model.addAttribute("statuts", StatutMemoire.values())
        model.addAttribute("types", TypeMemoire.values())
        model.addAttribute("selectedStatut", statut)
        model.addAttribute("selectedType", type)
        return "memoires/list"
    }

    @GetMapping("/new")
    fun showCreateForm(model: Model): String {
        model.addAttribute("memoire", Memoire())
        model.addAttribute("etudiants", etudiantService.findAll())
        model.addAttribute("professeurs", professeurService.findAll())
        model.addAttribute("statuts", StatutMemoire.values())
        model.addAttribute("types", TypeMemoire.values())
        return "memoires/form"
    }

    @PostMapping
    fun create(@Valid @ModelAttribute memoire: Memoire,
               bindingResult: BindingResult,
               model: Model,
               redirectAttributes: RedirectAttributes): String {
        if (bindingResult.hasErrors()) {
            model.addAttribute("etudiants", etudiantService.findAll())
            model.addAttribute("professeurs", professeurService.findAll())
            model.addAttribute("statuts", StatutMemoire.values())
            model.addAttribute("types", TypeMemoire.values())
            return "memoires/form"
        }

        try {
            memoireService.save(memoire)
            redirectAttributes.addFlashAttribute("successMessage", "Mémoire créé avec succès")
        } catch (e: IllegalArgumentException) {
            model.addAttribute("etudiants", etudiantService.findAll())
            model.addAttribute("professeurs", professeurService.findAll())
            model.addAttribute("statuts", StatutMemoire.values())
            model.addAttribute("types", TypeMemoire.values())
            bindingResult.rejectValue("etudiant", "error.memoire", e.message)
            return "memoires/form"
        }

        return "redirect:/memoires"
    }

    @GetMapping("/{id}")
    fun show(@PathVariable id: Long, model: Model): String {
        val memoire = memoireService.findById(id)
            ?: return "redirect:/memoires"
        
        model.addAttribute("memoire", memoire)
        return "memoires/show"
    }

    @GetMapping("/{id}/edit")
    fun showEditForm(@PathVariable id: Long, model: Model): String {
        val memoire = memoireService.findById(id)
            ?: return "redirect:/memoires"
        
        model.addAttribute("memoire", memoire)
        model.addAttribute("etudiants", etudiantService.findAll())
        model.addAttribute("professeurs", professeurService.findAll())
        model.addAttribute("statuts", StatutMemoire.values())
        model.addAttribute("types", TypeMemoire.values())
        return "memoires/form"
    }

    @PostMapping("/{id}")
    fun update(@PathVariable id: Long,
               @Valid @ModelAttribute memoire: Memoire,
               bindingResult: BindingResult,
               model: Model,
               redirectAttributes: RedirectAttributes): String {
        if (bindingResult.hasErrors()) {
            model.addAttribute("etudiants", etudiantService.findAll())
            model.addAttribute("professeurs", professeurService.findAll())
            model.addAttribute("statuts", StatutMemoire.values())
            model.addAttribute("types", TypeMemoire.values())
            return "memoires/form"
        }

        try {
            memoireService.update(id, memoire)
            redirectAttributes.addFlashAttribute("successMessage", "Mémoire modifié avec succès")
        } catch (e: IllegalArgumentException) {
            model.addAttribute("etudiants", etudiantService.findAll())
            model.addAttribute("professeurs", professeurService.findAll())
            model.addAttribute("statuts", StatutMemoire.values())
            model.addAttribute("types", TypeMemoire.values())
            bindingResult.rejectValue("etudiant", "error.memoire", e.message)
            return "memoires/form"
        }

        return "redirect:/memoires"
    }

    @PostMapping("/{id}/statut")
    fun updateStatut(@PathVariable id: Long,
                     @RequestParam statut: StatutMemoire,
                     redirectAttributes: RedirectAttributes): String {
        try {
            memoireService.updateStatut(id, statut)
            redirectAttributes.addFlashAttribute("successMessage", "Statut mis à jour avec succès")
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la mise à jour du statut")
        }
        return "redirect:/memoires/$id"
    }

    @PostMapping("/{id}/delete")
    fun delete(@PathVariable id: Long, redirectAttributes: RedirectAttributes): String {
        if (memoireService.deleteById(id)) {
            redirectAttributes.addFlashAttribute("successMessage", "Mémoire supprimé avec succès")
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression")
        }
        return "redirect:/memoires"
    }
}