package br.dev.saed.estapar.repositories

import br.dev.saed.estapar.entities.Spot
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface SpotRepository : JpaRepository<Spot, Long> {
    fun findSpotByLatAndLng(lat: Double, lng: Double): Spot?


}