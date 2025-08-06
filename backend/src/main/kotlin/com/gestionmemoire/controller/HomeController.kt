package com.gestionmemoire.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController {
    @GetMapping("/")
    fun home(model: Model): String {
        model.addAttribute("message", "Bienvenue sur la gestion des mémoires de stage !")
        return "index"
    }
}