package br.dev.saed.estapar.controllers

import br.dev.saed.estapar.dtos.response.GarageEntryResponse
import br.dev.saed.estapar.dtos.response.GarageOutResponse
import br.dev.saed.estapar.dtos.response.SpotEntryResponse
import br.dev.saed.estapar.services.GarageService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.time.Instant
import kotlin.test.Test

@ActiveProfiles("test")
@WebMvcTest
class GarageControllerTest(
    @Autowired
    val mockMvc: MockMvc,
) {
    @MockkBean
    lateinit var garageService: GarageService

    @Test
    fun `given a valid response when garage entry requested then return 201 Created`() {
        coEvery {
            garageService.handleEvent(any())
        } returns GarageEntryResponse(
            id = 1L,
            licensePlate = "ABC1234",
            entryTime = Instant.parse("2023-10-01T10:00:00Z"),
        )

        val mvcResult = mockMvc
            .perform(
                post("/webhook")
                    .content(
                    """{
                        "event_type": "ENTRY",
                        "license_plate": "ABC1234",
                        "entry_time": "2023-10-01T10:00:00"
                    }""".trimIndent()
                    )
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andReturn()

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.license_plate").value("ABC1234"))
            .andExpect(jsonPath("$.entry_time").value("2023-10-01T10:00:00Z"))
    }

    @Test
    fun `given a valid request when spot entry request then return 201 created`() {
        coEvery {
            garageService.handleEvent(any())
        } returns SpotEntryResponse(
            id = 1L,
            licensePlate = "ABC1234",
            lat = -23.5505,
            lng = -46.6333,
            occupation = 0.75f
        )

        val mvcResult = mockMvc
            .perform(
                post("/webhook")
                    .content(
                    """{
                        "event_type": "PARKED",
                        "license_plate": "ABC1234",
                        "lat": -23.5505,
                        "lng": -46.6333
                    }""".trimIndent()
                    )
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andReturn()

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.license_plate").value("ABC1234"))
            .andExpect(jsonPath("$.lat").value(-23.5505))
            .andExpect(jsonPath("$.lng").value(-46.6333))
            .andExpect(jsonPath("$.occupation").value(0.75f))
    }

    @Test
    fun `given a valid request when garage out request then return 201 created`() {
        coEvery {
            garageService.handleEvent(any())
        } returns GarageOutResponse(
            id = 1L,
            licensePlate = "ABC1234",
            exitTime = Instant.parse("2023-10-01T12:00:00Z"),
            totalValue = BigDecimal("50.0")
        )

        val mvcResult = mockMvc
            .perform(
                post("/webhook")
                    .content(
                    """{
                        "event_type": "EXIT",
                        "license_plate": "ABC1234",
                        "exit_time": "2023-10-01T12:00:00"
                    }""".trimIndent()
                    )
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andReturn()

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.license_plate").value("ABC1234"))
            .andExpect(jsonPath("$.exit_time").value("2023-10-01T12:00:00Z"))
            .andExpect(jsonPath("$.total_value").value("50.0"))
    }
}