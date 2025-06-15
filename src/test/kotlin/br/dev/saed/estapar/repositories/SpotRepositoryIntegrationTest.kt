package br.dev.saed.estapar.repositories

import br.dev.saed.estapar.entities.Sector
import br.dev.saed.estapar.entities.Spot
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ActiveProfiles("test")
@DataJpaTest
@Transactional
class SpotRepositoryIntegrationTest(

) {
    @Autowired
    lateinit var spotRepository: SpotRepository

    @Autowired
    lateinit var sectorRepository: SectorRepository


    @Test
    fun `given spot exists when findById then return spot`() {
        // Arrange
        val sector = Sector(
            sector = "A",
            basePrice = BigDecimal("10.00"),
            maxCapacity = 10,
            openHour = LocalTime.of(0, 0),
            closeHour = LocalTime.of(23, 59),
            durationLimitMinutes = 60
        )
        sectorRepository.save(sector)
        val spot = spotRepository.save(
            Spot(
                id = null,
                lat = -23.5505,
                lng = -46.6333,
                sector = sector,
                occupied = false,
            )
        )

        // Act
        val foundSpot = spotRepository.findByIdOrNull(spot.id!!)

        // Assert
        assertNotNull(foundSpot)
        assertEquals(expected = spot, actual = foundSpot)
    }

}