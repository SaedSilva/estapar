package br.dev.saed.estapar.repositories

import br.dev.saed.estapar.entities.GarageOut
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GarageOutRepository : JpaRepository<GarageOut, Long> {

}