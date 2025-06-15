package br.dev.saed.estapar.repositories

import br.dev.saed.estapar.entities.Sector
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ActiveProfiles("test")
@DataJpaTest
class SectorRepositoryIntegrationTest(

) {
    @Autowired
    lateinit var sectorRepository: SectorRepository

    @Test
    fun `given sector exists when findById then return sector`() {
        // Arrange
        val sector = sectorRepository.save(
            Sector(
                sector = "A",
                basePrice = BigDecimal("10.00"),
                maxCapacity = 10,
                openHour = LocalTime.of(0, 0),
                closeHour = LocalTime.of(23, 59),
                durationLimitMinutes = 60
            )
        )

        // Act
        val foundSector = sectorRepository.findByIdOrNull(sector.sector!!)

        // Assert
        assertNotNull(foundSector)
        assertEquals(expected = sector, actual = foundSector)
    }

    @Test
    fun `given actual occupation when getActualOccupation then return correct value`() {
        // Arrange
        val sector = sectorRepository.save(
            Sector(
                sector = "B",
                basePrice = BigDecimal("15.00"),
                maxCapacity = 5,
                openHour = LocalTime.of(0, 0),
                closeHour = LocalTime.of(23, 59),
                durationLimitMinutes = 120
            )
        )

        // Act
        val actualOccupation = sectorRepository.getActualOccupation(sector.sector!!)

        // Assert
        assertEquals(expected = 0f, actual = actualOccupation)
    }

}