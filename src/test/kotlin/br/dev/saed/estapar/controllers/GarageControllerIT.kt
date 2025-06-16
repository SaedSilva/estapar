package br.dev.saed.estapar.controllers

import br.dev.saed.estapar.dtos.request.*
import br.dev.saed.estapar.dtos.response.*
import br.dev.saed.estapar.services.GarageService
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GarageControllerIT(
) {


    @Autowired
    lateinit var restTemplate: TestRestTemplate



    @Test
    fun `when garage entry and spot entry and garage out are requested sequentially then all return 201 Created`() {
        val garageEntryResponse = restTemplate.postForEntity(
            "/webhook",
            garageEntryRequest,
            GarageEntryResponse::class.java
        )
        val garageEntryEntity = garageEntryResponse.body

        val spotEntryResponse = restTemplate.postForEntity(
            "/webhook",
            spotEntryRequest,
            SpotEntryResponse::class.java
        )
        val spotEntryEntity = spotEntryResponse.body

        val garageOutResponse = restTemplate.postForEntity(
            "/webhook",
            garageOutRequest,
            GarageOutResponse::class.java
        )
        val garageOutEntity = garageOutResponse.body

        assertEquals(HttpStatus.CREATED, garageEntryResponse.statusCode)
        assertNotNull(garageEntryEntity)
        assertEquals("ABC1234", garageEntryEntity.licensePlate)

        assertEquals(HttpStatus.CREATED, spotEntryResponse.statusCode)
        assertNotNull(spotEntryEntity)
        assertEquals("ABC1234", spotEntryEntity.licensePlate)

        assertEquals(HttpStatus.CREATED, garageOutResponse.statusCode)
        assertNotNull(garageOutEntity)
        assertEquals("ABC1234", garageOutEntity.licensePlate)
    }

    @Test
    fun `when garage entry, spot entry, plate status check, and garage out are requested sequentially then return expected statuses`() {
        val plateStatusRequest = LicensePlateStatusRequest(
            licensePlate = "ABC1234",
        )
        val garageEntryResponse = restTemplate.postForEntity(
            "/webhook",
            garageEntryRequest,
            GarageEntryResponse::class.java
        )
        val garageEntryEntity = garageEntryResponse.body

        val spotEntryResponse = restTemplate.postForEntity(
            "/webhook",
            spotEntryRequest,
            SpotEntryResponse::class.java
        )
        val spotEntryEntity = spotEntryResponse.body
        val plateStatusResponse = restTemplate.postForEntity(
            "/plate-status",
            plateStatusRequest,
            LicensePlateStatusResponse::class.java
        )
        val plateStatusEntity = plateStatusResponse.body

        val garageOutResponse = restTemplate.postForEntity(
            "/webhook",
            garageOutRequest,
            GarageOutResponse::class.java
        )
        val garageOutEntity = garageOutResponse.body

        assertEquals(HttpStatus.CREATED, garageEntryResponse.statusCode)
        assertNotNull(garageEntryEntity)
        assertEquals("ABC1234", garageEntryEntity.licensePlate)

        assertEquals(HttpStatus.CREATED, spotEntryResponse.statusCode)
        assertNotNull(spotEntryEntity)
        assertEquals("ABC1234", spotEntryEntity.licensePlate)

        assertEquals(HttpStatus.OK, plateStatusResponse.statusCode)
        assertNotNull(plateStatusEntity)
        assertEquals("ABC1234", plateStatusEntity.licensePlate)

        assertEquals(HttpStatus.CREATED, garageOutResponse.statusCode)
        assertNotNull(garageOutEntity)
        assertEquals("ABC1234", garageOutEntity.licensePlate)
    }
    @Test
    fun `when garage entry, spot entry, spot status check, and garage out are requested sequentially then return 201 201 200 201 respectively`() {
        val spotStatusRequest = SpotStatusRequest(
            lat = -23.5505,
            lng = -46.6333
        )
        val garageEntryResponse = restTemplate.postForEntity(
            "/webhook",
            garageEntryRequest,
            GarageEntryResponse::class.java
        )
        val garageEntryEntity = garageEntryResponse.body

        val spotEntryResponse = restTemplate.postForEntity(
            "/webhook",
            spotEntryRequest,
            SpotEntryResponse::class.java
        )
        val spotEntryEntity = spotEntryResponse.body

        val spotStatusResponse = restTemplate.postForEntity(
            "/spot-status",
            spotStatusRequest,
            SpotStatusResponse::class.java
        )
        val spotStatusEntity = spotStatusResponse.body

        val garageOutResponse = restTemplate.postForEntity(
            "/webhook",
            garageOutRequest,
            GarageOutResponse::class.java
        )
        val garageOutEntity = garageOutResponse.body

        assertEquals(HttpStatus.CREATED, garageEntryResponse.statusCode)
        assertNotNull(garageEntryEntity)
        assertEquals("ABC1234", garageEntryEntity.licensePlate)

        assertEquals(HttpStatus.CREATED, spotEntryResponse.statusCode)
        assertNotNull(spotEntryEntity)
        assertEquals("ABC1234", spotEntryEntity.licensePlate)

        assertEquals(HttpStatus.OK, spotStatusResponse.statusCode)
        assertNotNull(spotStatusEntity)
        assertEquals(true, spotStatusEntity.occupied)
        assertEquals("ABC1234", spotStatusEntity.licensePlate)

        assertEquals(HttpStatus.CREATED, garageOutResponse.statusCode)
        assertNotNull(garageOutEntity)
        assertEquals("ABC1234", garageOutEntity.licensePlate)
    }

    companion object {
        val garageEntryRequest = GarageEntryRequest(
            entryTime = LocalDateTime.parse("2025-06-16T10:00:00"),
            licensePlate = "ABC1234"
        ) as WebHookRequest

        val spotEntryRequest = SpotEntryRequest(
            lat = -23.5505,
            lng = -46.6333,
            licensePlate = "ABC1234"
        ) as WebHookRequest

        val garageOutRequest = GarageOutRequest(
            exitTime = LocalDateTime.parse("2025-06-16T12:00:00"),
            licensePlate = "ABC1234"
        ) as WebHookRequest

        @BeforeAll
        @JvmStatic
        fun setup(
            @Autowired
            garageService: GarageService
        ) {
            val response = GarageResponse(
                garage = listOf(
                    SectorResponse(
                        sector = "A",
                        basePrice = 10.0,
                        maxCapacity = 100,
                        openHour = "00:00",
                        closeHour = "23:59",
                        durationLimitMinutes = 60
                    ),
                    SectorResponse(
                        sector = "B",
                        basePrice = 20.0,
                        maxCapacity = 10,
                        openHour = "00:00",
                        closeHour = "23:59",
                        durationLimitMinutes = 120
                    )
                ),
                spots = listOf(
                    SpotResponse(
                        id = 1,
                        lat = -23.5505,
                        lng = -46.6333,
                        sector = "A",
                        occupied = false
                    ),
                    SpotResponse(
                        id = 2,
                        lat = -23.5510,
                        lng = -46.6340,
                        sector = "B",
                        occupied = false
                    )
                )
            )

            runBlocking {
                garageService.setupInitialData(response)
                for (i in 0..3) {
                    garageService.garageEntry(garageEntryRequest as GarageEntryRequest)
                    garageService.spotEntry(spotEntryRequest as SpotEntryRequest)
                    garageService.garageOut(garageOutRequest as GarageOutRequest)
                }
            }
        }

    }
}