package br.dev.saed.estapar.repositories

import br.dev.saed.estapar.entities.SpotEntry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SpotEntryRepository: JpaRepository<SpotEntry, Long> {
    fun findByGarageEntryLicensePlateAndGarageOutIsNull(licensePlate: String): SpotEntry?
}