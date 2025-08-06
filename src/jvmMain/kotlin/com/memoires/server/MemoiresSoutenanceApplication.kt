package com.memoires.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

/**
 * Classe principale de l'application de gestion des mémoires de soutenance.
 * 
 * @SpringBootApplication - Active l'auto-configuration Spring Boot, la configuration des composants
 * et la recherche automatique des packages
 * @EnableJpaAuditing - Active l'audit JPA pour le tracking automatique des dates de création/modification
 */
@SpringBootApplication
@EnableJpaAuditing
class MemoiresSoutenanceApplication

/**
 * Point d'entrée principal de l'application.
 * Démarre le serveur Spring Boot intégré avec la configuration par défaut.
 */
fun main(args: Array<String>) {
    runApplication<MemoiresSoutenanceApplication>(*args)
}