package br.dev.saed.estapar.dtos.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.Instant

data class SpotStatusResponse(
    val occupied: Boolean,
    @JsonProperty("license_plate")
    val licensePlate: String?,
    @JsonProperty("price_until_now")
    val priceUntilNow: BigDecimal,
    @JsonProperty("entry_time")
    val entryTime: Instant?,
    @JsonProperty("time_parked")
    val timeParked: String?,
)
