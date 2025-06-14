package br.dev.saed.estapar.services

import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import kotlin.test.assertEquals

class GarageCalculatorServiceTest {
    private val garageCalculatorService: GarageCalculatorService = GarageCalculatorService()

    @Test
    fun `test calculateValue with low occupation`() {
        val occupation = 0.2f
        val basePrice = BigDecimal("10.00")
        val entryTime = Instant.parse("2023-10-01T10:00:00Z")
        val outTime = Instant.parse("2023-10-01T12:00:00Z")

        val actual = garageCalculatorService.calculateValue(occupation, basePrice, entryTime, outTime)
        val expected = BigDecimal("18.00")

        assertEquals(expected, actual) // 2 hours * 10.00 * 0.9
    }

    @Test
    fun `test calculateValue with normal occupation`() {
        val occupation = 0.4f
        val basePrice = BigDecimal("10.00")
        val entryTime = Instant.parse("2023-10-01T10:00:00Z")
        val outTime = Instant.parse("2023-10-01T12:00:00Z")

        val actual = garageCalculatorService.calculateValue(occupation, basePrice, entryTime, outTime)
        val expected = BigDecimal("20.00") // 2 hours * 10.00 * 1.0

        assertEquals(expected, actual)
    }

    @Test
    fun `test calculateValue with high occupation`() {
        val occupation = 0.6f
        val basePrice = BigDecimal("10.00")
        val entryTime = Instant.parse("2023-10-01T10:00:00Z")
        val outTime = Instant.parse("2023-10-01T12:00:00Z")

        val actual = garageCalculatorService.calculateValue(occupation, basePrice, entryTime, outTime)
        val expected = BigDecimal("22.00") // 2 hours * 10.00 * 1.1

        assertEquals(expected, actual)
    }

    @Test
    fun `test calculateValue with very high occupation`() {
        val occupation = 0.8f
        val basePrice = BigDecimal("10.00")
        val entryTime = Instant.parse("2023-10-01T10:00:00Z")
        val outTime = Instant.parse("2023-10-01T12:00:00Z")

        val actual = garageCalculatorService.calculateValue(occupation, basePrice, entryTime, outTime)
        val expected = BigDecimal("25.00") // 2 hours * 10.00 * 1.25

        assertEquals(expected, actual)
    }
}