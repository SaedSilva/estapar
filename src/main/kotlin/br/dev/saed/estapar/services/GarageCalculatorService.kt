package br.dev.saed.estapar.services

import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Duration
import java.time.Instant


/**
 * Service for calculating the value of parking based on occupation, base price, entry time, and out time.
 */

@Service
class GarageCalculatorService {

    /**
     * Calculates the total parking value based on the given parameters.
     * @param occupation The current occupation percentage of the garage (0 to 100).
     * @param basePrice The base price per hour for parking.
     * @param entryTime The time when the vehicle entered the garage.
     * @param outTime The time when the vehicle left the garage.
     * @return The total calculated price after applying dynamic pricing based on the garage's occupation.
     */
    internal fun calculateValue(
        occupation: Float,
        basePrice: BigDecimal,
        entryTime: Instant,
        outTime: Instant
    ): BigDecimal {
        val hours = Duration.between(entryTime, outTime).toSeconds() / 3600.0
        val totalPrice = basePrice.multiply(BigDecimal.valueOf(hours))
        return applyDynamicPrice(totalPrice, occupation).setScale(2, RoundingMode.DOWN)
    }

    /**
     * Applies a dynamic price adjustment based on the current occupation of the garage.
     * @param value The calculated value before applying dynamic pricing.
     * @param occupation The current occupation percentage of the garage (0 to 100).
     * @return The adjusted price after applying the dynamic pricing factor.
     */
    private fun applyDynamicPrice(value: BigDecimal, occupation: Float): BigDecimal {
        val dynamicPriceFactor = DynamicPriceFactor.fromOccupation(occupation)
        return value.multiply(dynamicPriceFactor.factor)
    }

    /**
     * Enum representing the dynamic price factors based on garage occupation.
     * Each factor corresponds to a different level of occupation and adjusts the price accordingly.
     */

    private enum class DynamicPriceFactor(val factor: BigDecimal) {
        LOW(BigDecimal("0.9")),
        NORMAL(BigDecimal("1.0")),
        HIGH(BigDecimal("1.10")),
        VERY_HIGH(BigDecimal("1.25"));

        companion object {
            fun fromOccupation(occupation: Float): DynamicPriceFactor {
                return when {
                    occupation < 0.25f -> LOW
                    occupation < 0.50f -> NORMAL
                    occupation < 0.75f -> HIGH
                    occupation <= 100.0f -> VERY_HIGH
                    else -> throw IllegalArgumentException("Occupation percentage must be between 0 and 100")
                }
            }
        }
    }
}