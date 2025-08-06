package com.gestionmemoire.repository

import com.gestionmemoire.model.Memoire
import org.springframework.data.jpa.repository.JpaRepository

interface MemoireRepository : JpaRepository<Memoire, Long>