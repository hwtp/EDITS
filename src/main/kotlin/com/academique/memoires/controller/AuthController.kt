package com.academique.memoires.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class AuthController {

    @GetMapping("/login")
    fun login(@RequestParam(required = false) error: String?,
              @RequestParam(required = false) logout: String?,
              model: Model): String {
        
        if (error != null) {
            model.addAttribute("errorMessage", "Nom d'utilisateur ou mot de passe invalide")
        }
        
        if (logout != null) {
            model.addAttribute("successMessage", "Vous avez été déconnecté avec succès")
        }
        
        return "auth/login"
    }
}