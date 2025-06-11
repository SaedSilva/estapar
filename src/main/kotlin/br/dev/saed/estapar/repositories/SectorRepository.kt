package br.dev.saed.estapar.repositories

import br.dev.saed.estapar.entities.Sector
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface SectorRepository : JpaRepository<Sector, String> {
    @Query(
        nativeQuery = true, value = """
    SELECT (COUNT(*)::real / (SELECT max_capacity FROM tb_sector WHERE sector = ?1)) AS capacity
    FROM tb_spot AS ts
    WHERE ts.sector = ?1
      AND ts.occupied = true;
    """
    )
    fun getActualOccupation(sectorId: String): Float
}