package br.dev.saed.estapar.services

import br.dev.saed.estapar.dtos.request.GarageEntryRequest
import br.dev.saed.estapar.dtos.request.GarageOutRequest
import br.dev.saed.estapar.dtos.request.SpotEntryRequest
import br.dev.saed.estapar.entities.GarageEntry
import br.dev.saed.estapar.entities.Sector
import br.dev.saed.estapar.entities.Spot
import br.dev.saed.estapar.entities.SpotEntry
import br.dev.saed.estapar.repositories.*
import br.dev.saed.estapar.services.execeptions.SectorLimitExceededException
import br.dev.saed.estapar.services.execeptions.SpotOccupiedException
import io.mockk.every
import io.mockk.mockk
import jakarta.persistence.EntityNotFoundException
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@ActiveProfiles("test")
class GarageServiceTest {

    private val sectorRepository: SectorRepository = mockk()
    private val spotRepository: SpotRepository = mockk()
    private val garageEntryRepository: GarageEntryRepository = mockk()
    private val spotEntryRepository: SpotEntryRepository = mockk()
    private val garageOutRepository: GarageOutRepository = mockk()
    private val garageCalculatorService: GarageCalculatorService = mockk()

    private lateinit var service: GarageService

    private val sampleSector = Sector(
        sector = "A",
        basePrice = BigDecimal("5.00"),
        maxCapacity = 10,
        openHour = LocalTime.of(8, 0),
        closeHour = LocalTime.of(22, 0),
        durationLimitMinutes = 120
    )

    private val sampleSpot = Spot(
        id = 1L,
        sector = sampleSector,
        lat = -23.5505,
        lng = -46.6333,
        occupied = false
    )

    @BeforeEach
    fun setup() {
        service = GarageService(
            sectorRepository = sectorRepository,
            spotRepository = spotRepository,
            garageEntryRepository = garageEntryRepository,
            spotEntryRepository = spotEntryRepository,
            garageOutRepository = garageOutRepository,
            garageCalculatorService = garageCalculatorService
        )
    }

    @Test
    fun `given no active entry when garage entry requested then save and return entry`() = runBlocking {
        val request = GarageEntryRequest(
            licensePlate = "ABC1234",
            entryTime = LocalDateTime.of(2025, 1, 1, 10, 0)
        )

        every { garageEntryRepository.findGarageEntryByLicensePlateAndGarageOutIsNull(licensePlate = "ABC1234") } returns null
        every { garageEntryRepository.save(any()) } answers { firstArg() }

        val result = service.garageEntry(request)

        assertEquals("ABC1234", result.licensePlate)
        assertEquals("2025-01-01T10:00:00Z", result.entryTime.toString())
    }

    @Test
    fun `given active entry exists when garage entry requested then throw EntityNotFoundException`() {
        runBlocking {
            val request = GarageEntryRequest(
                licensePlate = "ABC1234",
                entryTime = LocalDateTime.of(2025, 1, 1, 10, 0)
            )

            every { garageEntryRepository.findGarageEntryByLicensePlateAndGarageOutIsNull(licensePlate = "ABC1234") } returns GarageEntry(
                licensePlate = "ABC1234",
                entryTime = Instant.parse("2025-01-01T10:00:00Z")
            )

            assertFailsWith<EntityNotFoundException> {
                service.garageEntry(request)
            }
        }
    }

    @Test
    fun `given available spot when spot entry requested then assign spot`() = runBlocking {
        val request = SpotEntryRequest(
            licensePlate = "ABC1234",
            lat = -23.5505,
            lng = -46.6333
        )

        every { spotRepository.findSpotByLatAndLng(lat = -23.5505, lng = -46.6333) } returns sampleSpot
        every { sectorRepository.getActualOccupation("A") } returns 0.5f
        every { garageEntryRepository.findGarageEntryByLicensePlateAndGarageOutIsNull(licensePlate = "ABC1234") } returns GarageEntry(
            licensePlate = "ABC1234",
            entryTime = Instant.now()
        )
        every { spotRepository.save(any()) } answers { firstArg() }
        every { spotEntryRepository.save(any()) } answers { firstArg() }

        val result = service.spotEntry(request)

        assertEquals("ABC1234", result.licensePlate)
        assertEquals(-23.5505, result.lat)
        assertEquals(-46.6333, result.lng)
    }

    @Test
    fun `given occupied spot when spot entry requested then throw SpotOccupiedException`() {
        runBlocking {
            val request = SpotEntryRequest(
                licensePlate = "ABC1234",
                lat = -23.5505,
                lng = -46.6333
            )

            every { spotRepository.findSpotByLatAndLng(lat = -23.5505, lng = -46.6333) } returns sampleSpot.let {
                Spot(
                    id = it.id,
                    sector = it.sector,
                    lat = it.lat,
                    lng = it.lng,
                    occupied = true // Simulating that the spot is occupied
                )
            }

            assertFailsWith<SpotOccupiedException> {
                service.spotEntry(request)
            }
        }
    }

    @Test
    fun `given sector at full capacity when spot entry requested then throw SectorLimitExceededException`() {
        runBlocking {
            val request = SpotEntryRequest(
                licensePlate = "ABC1234",
                lat = -23.5505,
                lng = -46.6333
            )

            every { spotRepository.findSpotByLatAndLng(lat = -23.5505, lng = -46.6333) } returns sampleSpot
            every { sectorRepository.getActualOccupation("A") } returns 100.0f

            assertFailsWith<SectorLimitExceededException> {
                service.spotEntry(request)
            }
        }
    }

    @Test
    fun `given valid garage out request when garage out called then return calculated value`() = runBlocking {
        val request = GarageOutRequest(
            licensePlate = "ABC1234",
            exitTime = LocalDateTime.of(2025, 1, 1, 12, 0)
        )

        val garageEntry = GarageEntry(
            licensePlate = "ABC1234",
            entryTime = Instant.parse("2025-01-01T10:00:00Z")
        )

        val spotEntry = SpotEntry(
            id = 1L,
            actualOccupation = 0.5f,
            spot = sampleSpot,
            garageEntry = garageEntry
        )

        every { garageEntryRepository.findGarageEntryByLicensePlateAndGarageOutIsNull(licensePlate = "ABC1234") } returns garageEntry
        every { spotEntryRepository.findByGarageEntryLicensePlateAndGarageOutIsNull(licensePlate = "ABC1234") } returns spotEntry
        every { garageCalculatorService.calculateValue(any(), any(), any(), any()) } returns BigDecimal("10.00")
        every { garageOutRepository.save(any()) } answers { firstArg() }
        every { spotRepository.save(any()) } answers { firstArg() }

        val result = service.garageOut(request)

        assertEquals("ABC1234", result.licensePlate)
        assertEquals(BigDecimal("10.00"), result.totalValue)
        assertEquals("2025-01-01T12:00:00Z", result.exitTime.toString())
    }

    @Test
    fun `given no spot entry when garage out requested then total value should be zero`() = runBlocking {
        val request = GarageOutRequest(
            licensePlate = "XYZ5678",
            exitTime = LocalDateTime.of(2025, 1, 1, 12, 0)
        )

        val garageEntry = GarageEntry(
            licensePlate = "XYZ5678",
            entryTime = Instant.parse("2025-01-01T10:00:00Z")
        )

        every { garageEntryRepository.findGarageEntryByLicensePlateAndGarageOutIsNull(licensePlate = "XYZ5678") } returns garageEntry
        every { spotEntryRepository.findByGarageEntryLicensePlateAndGarageOutIsNull(licensePlate = "XYZ5678") } returns null
        every { garageOutRepository.save(any()) } answers { firstArg() }

        val result = service.garageOut(request)

        assertEquals("XYZ5678", result.licensePlate)
        assertEquals(BigDecimal.ZERO, result.totalValue)
        assertEquals("2025-01-01T12:00:00Z", result.exitTime.toString())
    }
}
