package br.dev.saed.estapar.controllers

import br.dev.saed.estapar.dtos.request.GarageEntryRequest
import br.dev.saed.estapar.dtos.request.GarageOutRequest
import br.dev.saed.estapar.dtos.request.SpotEntryRequest
import br.dev.saed.estapar.dtos.request.WebHookRequest
import br.dev.saed.estapar.dtos.response.GarageEntryResponse
import br.dev.saed.estapar.dtos.response.GarageOutResponse
import br.dev.saed.estapar.dtos.response.GarageResponse
import br.dev.saed.estapar.dtos.response.SectorResponse
import br.dev.saed.estapar.dtos.response.SpotEntryResponse
import br.dev.saed.estapar.dtos.response.SpotResponse
import br.dev.saed.estapar.services.GarageService
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GarageControllerIT(
) {


    @Autowired
    lateinit var restTemplate: TestRestTemplate

    val garageEntryRequest = GarageEntryRequest(
        entryTime = LocalDateTime.parse("2023-10-01T10:00:00"),
        licensePlate = "ABC1234"
    ) as WebHookRequest

    val spotEntryRequest = SpotEntryRequest(
        lat = -23.5505,
        lng = -46.6333,
        licensePlate = "ABC1234"
    ) as WebHookRequest

    val garageOutRequest = GarageOutRequest(
        exitTime = LocalDateTime.parse("2023-10-01T12:00:00"),
        licensePlate = "ABC1234"
    ) as WebHookRequest

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
        assertEquals(1L, garageEntryEntity.id)
        assertEquals("ABC1234", garageEntryEntity.licensePlate)

        assertEquals(HttpStatus.CREATED, spotEntryResponse.statusCode)
        assertNotNull(spotEntryEntity)
        assertEquals(1L, spotEntryEntity.id)
        assertEquals("ABC1234", spotEntryEntity.licensePlate)

        assertEquals(HttpStatus.CREATED, garageOutResponse.statusCode)
        assertNotNull(garageOutEntity)
        assertEquals(1L, garageOutEntity.id)
        assertEquals("ABC1234", garageOutEntity.licensePlate)
    }

    companion object {
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
            }
        }

    }
}