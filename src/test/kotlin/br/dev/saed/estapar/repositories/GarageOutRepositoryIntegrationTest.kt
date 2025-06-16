package br.dev.saed.estapar.repositories

import br.dev.saed.estapar.entities.GarageEntry
import br.dev.saed.estapar.entities.GarageOut
import br.dev.saed.estapar.entities.Sector
import br.dev.saed.estapar.entities.Spot
import br.dev.saed.estapar.entities.SpotEntry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ActiveProfiles("test")
@DataJpaTest
@Transactional
class GarageOutRepositoryIntegrationTest(

) {
    @Autowired
    lateinit var garageOutRepository: GarageOutRepository

    @Autowired
    lateinit var garageEntryRepository: GarageEntryRepository

    @Autowired
    lateinit var spotEntryRepository: SpotEntryRepository

    @Autowired
    lateinit var spotRepository: SpotRepository

    @Autowired
    lateinit var sectorRepository: SectorRepository

    @Test
    fun `given total value exists when findTotalValueBySectorAndExitDate then return total value`() {
        // Arrange

        val sector = sectorRepository.save(Sector(
            sector = "A",
            basePrice = BigDecimal("10.00"),
            maxCapacity = 10,
            openHour = LocalTime.of(0, 0),
            closeHour = LocalTime.of(23, 59),
            durationLimitMinutes = 60
        ))

        val spot = spotRepository.save(
            Spot(
                id = null,
                lat = -23.5505,
                lng = -46.6333,
                sector = sector,
                occupied = false,
            )
        )

        val garageEntry = garageEntryRepository.save(
            GarageEntry(
                id = null,
                licensePlate = "ABC1234",
                entryTime = Instant.parse("2023-10-01T10:00:00Z"),
            )
        )

        val spotEntry = spotEntryRepository.save(
            SpotEntry(
                id = null,
                spot = spot,
                actualOccupation = 0.0f,
                garageEntry = garageEntry
            )
        )

        val garageOut = garageOutRepository.save(
            GarageOut(
                id = null,
                spotEntry = spotEntry,
                exitTime = Instant.parse("2023-10-01T12:00:00Z"),
                totalValue = BigDecimal("20.00"),
                garageEntry = garageEntry
            )
        )

        // Act
        val totalValue = garageOutRepository.findTotalValueBySectorAndExitDate(sector.sector!!, LocalDate.parse("2023-10-01"))
        // Assert
        assertNotNull(totalValue)
        assertEquals(expected = BigDecimal("20.00"), actual = totalValue)
        assertEquals(expected = garageOut.totalValue, actual = totalValue)

    }
}