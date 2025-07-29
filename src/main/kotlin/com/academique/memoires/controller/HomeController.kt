package com.academique.memoires.controller

import com.academique.memoires.entity.StatutMemoire
import com.academique.memoires.entity.StatutSoutenance
import com.academique.memoires.service.EtudiantService
import com.academique.memoires.service.MemoireService
import com.academique.memoires.service.ProfesseurService
import com.academique.memoires.service.SoutenanceService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController(
    private val etudiantService: EtudiantService,
    private val professeurService: ProfesseurService,
    private val memoireService: MemoireService,
    private val soutenanceService: SoutenanceService
) {

    @GetMapping("/")
    fun home(model: Model): String {
        // Statistiques pour le dashboard
        val totalEtudiants = etudiantService.findAll().size
        val totalProfesseurs = professeurService.findAll().size
        val totalMemoires = memoireService.findAll().size
        val totalSoutenances = soutenanceService.findAll().size
        
        // Mémoires par statut
        val memoiresEnCours = memoireService.findByStatut(StatutMemoire.EN_COURS).size
        val memoiresDeposes = memoireService.findByStatut(StatutMemoire.DEPOSE).size
        val memoiresValides = memoireService.findByStatut(StatutMemoire.VALIDE).size
        val memoiresSoutenus = memoireService.findByStatut(StatutMemoire.SOUTENU).size
        
        // Soutenances par statut
        val soutenancesProgrammees = soutenanceService.findByStatut(StatutSoutenance.PROGRAMMEE).size
        val soutenancesTerminees = soutenanceService.findByStatut(StatutSoutenance.TERMINEE).size
        
        // Prochaines soutenances
        val prochainesSoutenances = soutenanceService.findByStatutOrderByDate(StatutSoutenance.PROGRAMMEE).take(5)
        
        model.addAttribute("totalEtudiants", totalEtudiants)
        model.addAttribute("totalProfesseurs", totalProfesseurs)
        model.addAttribute("totalMemoires", totalMemoires)
        model.addAttribute("totalSoutenances", totalSoutenances)
        model.addAttribute("memoiresEnCours", memoiresEnCours)
        model.addAttribute("memoiresDeposes", memoiresDeposes)
        model.addAttribute("memoiresValides", memoiresValides)
        model.addAttribute("memoiresSoutenus", memoiresSoutenus)
        model.addAttribute("soutenancesProgrammees", soutenancesProgrammees)
        model.addAttribute("soutenancesTerminees", soutenancesTerminees)
        model.addAttribute("prochainesSoutenances", prochainesSoutenances)
        
        return "index"
    }
}