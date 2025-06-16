package br.dev.saed.estapar.repositories

import br.dev.saed.estapar.entities.GarageOut
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDate

@Repository
interface GarageOutRepository : JpaRepository<GarageOut, Long> {
    @Query(
        nativeQuery = true, value = """
        SELECT SUM(GO.total_value)
        FROM tb_garage_out AS GO
        INNER JOIN tb_spot_entry AS SE ON GO.spot_entry_id = SE.id
        INNER JOIN tb_spot AS SP ON SE.spot_id = SP.id
        INNER JOIN tb_sector AS SCT ON SP.sector = SCT.sector
        WHERE SCT.sector = ?1 AND CAST(GO.exit_time AS date) = ?2;
    """
    )
    // Cast foi usado para manter a compatibilidade entre o H2 e o PostgreSQL

    fun findTotalValueBySectorAndExitDate(sector: String, date: LocalDate): BigDecimal?
}