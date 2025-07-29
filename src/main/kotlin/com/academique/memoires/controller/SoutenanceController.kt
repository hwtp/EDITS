package com.academique.memoires.controller

import com.academique.memoires.entity.MentionSoutenance
import com.academique.memoires.entity.Soutenance
import com.academique.memoires.entity.StatutSoutenance
import com.academique.memoires.service.JuryService
import com.academique.memoires.service.MemoireService
import com.academique.memoires.service.SoutenanceService
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.time.LocalDateTime

@Controller
@RequestMapping("/soutenances")
class SoutenanceController(
    private val soutenanceService: SoutenanceService,
    private val memoireService: MemoireService,
    private val juryService: JuryService
) {

    @GetMapping
    fun list(@RequestParam(required = false) statut: StatutSoutenance?,
             @RequestParam(required = false) salle: String?,
             model: Model): String {
        
        val soutenances = when {
            statut != null -> soutenanceService.findByStatut(statut)
            !salle.isNullOrBlank() -> soutenanceService.findBySalle(salle)
            else -> soutenanceService.findAll()
        }
        
        model.addAttribute("soutenances", soutenances)
        model.addAttribute("statuts", StatutSoutenance.values())
        model.addAttribute("selectedStatut", statut)
        model.addAttribute("salle", salle ?: "")
        return "soutenances/list"
    }

    @GetMapping("/new")
    fun showCreateForm(model: Model): String {
        model.addAttribute("soutenance", Soutenance())
        model.addAttribute("memoires", memoireService.findAll().filter { it.soutenance == null })
        model.addAttribute("jurys", juryService.findAll())
        model.addAttribute("statuts", StatutSoutenance.values())
        return "soutenances/form"
    }

    @PostMapping
    fun create(@Valid @ModelAttribute soutenance: Soutenance,
               bindingResult: BindingResult,
               model: Model,
               redirectAttributes: RedirectAttributes): String {
        if (bindingResult.hasErrors()) {
            model.addAttribute("memoires", memoireService.findAll().filter { it.soutenance == null })
            model.addAttribute("jurys", juryService.findAll())
            model.addAttribute("statuts", StatutSoutenance.values())
            return "soutenances/form"
        }

        try {
            soutenanceService.save(soutenance)
            redirectAttributes.addFlashAttribute("successMessage", "Soutenance créée avec succès")
        } catch (e: IllegalArgumentException) {
            model.addAttribute("memoires", memoireService.findAll().filter { it.soutenance == null })
            model.addAttribute("jurys", juryService.findAll())
            model.addAttribute("statuts", StatutSoutenance.values())
            bindingResult.rejectValue("salle", "error.soutenance", e.message)
            return "soutenances/form"
        }

        return "redirect:/soutenances"
    }

    @GetMapping("/{id}")
    fun show(@PathVariable id: Long, model: Model): String {
        val soutenance = soutenanceService.findById(id)
            ?: return "redirect:/soutenances"
        
        model.addAttribute("soutenance", soutenance)
        model.addAttribute("mentions", MentionSoutenance.values())
        return "soutenances/show"
    }

    @GetMapping("/{id}/edit")
    fun showEditForm(@PathVariable id: Long, model: Model): String {
        val soutenance = soutenanceService.findById(id)
            ?: return "redirect:/soutenances"
        
        model.addAttribute("soutenance", soutenance)
        model.addAttribute("memoires", memoireService.findAll())
        model.addAttribute("jurys", juryService.findAll())
        model.addAttribute("statuts", StatutSoutenance.values())
        return "soutenances/form"
    }

    @PostMapping("/{id}")
    fun update(@PathVariable id: Long,
               @Valid @ModelAttribute soutenance: Soutenance,
               bindingResult: BindingResult,
               model: Model,
               redirectAttributes: RedirectAttributes): String {
        if (bindingResult.hasErrors()) {
            model.addAttribute("memoires", memoireService.findAll())
            model.addAttribute("jurys", juryService.findAll())
            model.addAttribute("statuts", StatutSoutenance.values())
            return "soutenances/form"
        }

        try {
            soutenanceService.update(id, soutenance)
            redirectAttributes.addFlashAttribute("successMessage", "Soutenance modifiée avec succès")
        } catch (e: IllegalArgumentException) {
            model.addAttribute("memoires", memoireService.findAll())
            model.addAttribute("jurys", juryService.findAll())
            model.addAttribute("statuts", StatutSoutenance.values())
            bindingResult.rejectValue("salle", "error.soutenance", e.message)
            return "soutenances/form"
        }

        return "redirect:/soutenances"
    }

    @PostMapping("/{id}/statut")
    fun updateStatut(@PathVariable id: Long,
                     @RequestParam statut: StatutSoutenance,
                     redirectAttributes: RedirectAttributes): String {
        try {
            soutenanceService.updateStatut(id, statut)
            redirectAttributes.addFlashAttribute("successMessage", "Statut mis à jour avec succès")
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la mise à jour du statut")
        }
        return "redirect:/soutenances/$id"
    }

    @PostMapping("/{id}/note")
    fun updateNote(@PathVariable id: Long,
                   @RequestParam note: Double,
                   @RequestParam mention: MentionSoutenance,
                   redirectAttributes: RedirectAttributes): String {
        try {
            soutenanceService.updateNote(id, note, mention)
            redirectAttributes.addFlashAttribute("successMessage", "Note et mention mises à jour avec succès")
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la mise à jour de la note")
        }
        return "redirect:/soutenances/$id"
    }

    @PostMapping("/{id}/delete")
    fun delete(@PathVariable id: Long, redirectAttributes: RedirectAttributes): String {
        if (soutenanceService.deleteById(id)) {
            redirectAttributes.addFlashAttribute("successMessage", "Soutenance supprimée avec succès")
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression")
        }
        return "redirect:/soutenances"
    }

    @GetMapping("/planning")
    fun planning(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) date: LocalDateTime?,
                 model: Model): String {
        val targetDate = date ?: LocalDateTime.now()
        val soutenances = soutenanceService.findByDateSoutenance(targetDate)
        
        model.addAttribute("soutenances", soutenances)
        model.addAttribute("date", targetDate)
        return "soutenances/planning"
    }
}