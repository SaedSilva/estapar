package br.dev.saed.estapar.repositories

import br.dev.saed.estapar.entities.GarageEntry
import br.dev.saed.estapar.entities.Spot
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GarageEntryRepository: JpaRepository<GarageEntry, Long> {
    fun findGarageEntryByLicensePlateAndGarageOutIsNull(licensePlate: String): GarageEntry?
}