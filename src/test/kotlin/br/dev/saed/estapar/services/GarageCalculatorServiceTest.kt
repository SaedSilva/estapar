package br.dev.saed.estapar.services

import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.Instant
import kotlin.test.assertEquals

@ActiveProfiles("test")
class GarageCalculatorServiceTest {

    private val garageCalculatorService: GarageCalculatorService = GarageCalculatorService()

    @Test
    fun `given low occupation when calculateValue then apply discount`() {
        val occupation = 0.2f
        val basePrice = BigDecimal( "10.00")
        val entryTime: Instant = Instant.parse("2023-10-01T10:00:00Z")
        val outTime: Instant = Instant.parse("2023-10-01T12:00:00Z")

        val actual: BigDecimal = garageCalculatorService.calculateValue(
            occupation = occupation,
            basePrice = basePrice,
            entryTime = entryTime,
            outTime = outTime
        )

        val expected = BigDecimal( "18.00") // 2 hours * 10.00 * 0.9

        assertEquals(expected = expected, actual = actual)
    }

    @Test
    fun `given normal occupation when calculateValue then apply base price`() {
        val occupation = 0.4f
        val basePrice = BigDecimal("10.00")
        val entryTime: Instant = Instant.parse("2023-10-01T10:00:00Z")
        val outTime: Instant = Instant.parse("2023-10-01T12:00:00Z")

        val actual: BigDecimal = garageCalculatorService.calculateValue(
            occupation = occupation,
            basePrice = basePrice,
            entryTime = entryTime,
            outTime = outTime
        )

        val expected = BigDecimal("20.00") // 2 hours * 10.00 * 1.0

        assertEquals(expected = expected, actual = actual)
    }

    @Test
    fun `given high occupation when calculateValue then apply 10 percent surcharge`() {
        val occupation = 0.6f
        val basePrice = BigDecimal("10.00")
        val entryTime: Instant = Instant.parse("2023-10-01T10:00:00Z")
        val outTime: Instant = Instant.parse("2023-10-01T12:00:00Z")

        val actual: BigDecimal = garageCalculatorService.calculateValue(
            occupation = occupation,
            basePrice = basePrice,
            entryTime = entryTime,
            outTime = outTime
        )

        val expected = BigDecimal("22.00") // 2 hours * 10.00 * 1.1

        assertEquals(expected = expected, actual = actual)
    }

    @Test
    fun `given very high occupation when calculateValue then apply 25 percent surcharge`() {
        val occupation = 0.8f
        val basePrice = BigDecimal("10.00")
        val entryTime: Instant = Instant.parse("2023-10-01T10:00:00Z")
        val outTime: Instant = Instant.parse("2023-10-01T12:00:00Z")

        val actual: BigDecimal = garageCalculatorService.calculateValue(
            occupation = occupation,
            basePrice = basePrice,
            entryTime = entryTime,
            outTime = outTime
        )

        val expected = BigDecimal("25.00") // 2 hours * 10.00 * 1.25

        assertEquals(expected, actual)
    }
}
