package br.dev.saed.estapar.dtos.response

import br.dev.saed.estapar.entities.Sector
import br.dev.saed.estapar.entities.Spot
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.LocalTime

data class GarageResponse(
    val garage: List<SectorResponse>,
    val spots: List<SpotResponse>,
)

data class SectorResponse(
    val sector: String,
    @JsonProperty("base_price")
    val basePrice: Double,
    @JsonProperty("max_capacity")
    val maxCapacity: Int,
    @JsonProperty("open_hour")
    val openHour: String,
    @JsonProperty("close_hour")
    val closeHour: String,
    @JsonProperty("duration_limit_minutes")
    val durationLimitMinutes: Int,
)

fun SectorResponse.toEntity(): Sector {
    return Sector(
        sector = this.sector,
        basePrice = BigDecimal.valueOf(this.basePrice),
        maxCapacity = this.maxCapacity,
        openHour = LocalTime.parse(this.openHour),
        closeHour = LocalTime.parse(this.closeHour),
        durationLimitMinutes = this.durationLimitMinutes,
    )
}

data class SpotResponse(
    val id: Int,
    val sector: String,
    val lat: Double,
    val lng: Double,
    val occupied: Boolean,
)

fun SpotResponse.toEntity(): Spot {
    return Spot(
        lat = this.lat,
        lng = this.lng,
        occupied = this.occupied,
        sector = Sector(
            sector = this.sector,
            basePrice = BigDecimal.ZERO,
            maxCapacity = 0,
            openHour = LocalTime.MIN,
            closeHour = LocalTime.MAX,
            durationLimitMinutes = 0,
        ),
    )
}