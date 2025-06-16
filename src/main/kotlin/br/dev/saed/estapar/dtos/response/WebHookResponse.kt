package br.dev.saed.estapar.dtos.response

import br.dev.saed.estapar.entities.GarageEntry
import br.dev.saed.estapar.entities.GarageOut
import br.dev.saed.estapar.entities.SpotEntry
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.Instant


sealed class WebHookResponse

data class GarageEntryResponse(
    val id: Long,
    @JsonProperty("entry_time")
    val entryTime: Instant,
    @JsonProperty("license_plate")
    val licensePlate: String,
) : WebHookResponse() {
    companion object {
        fun fromEntity(entity: GarageEntry): GarageEntryResponse {
            return GarageEntryResponse(
                id = entity.id ?: 0L,
                entryTime = entity.entryTime,
                licensePlate = entity.licensePlate
            )
        }
    }
}

data class SpotEntryResponse(
    val id: Long,
    val lat: Double,
    @JsonProperty("license_plate")
    val licensePlate: String,
    val lng: Double,
    val occupation: Float,
) : WebHookResponse() {
    companion object {
        fun fromEntity(entity: SpotEntry): SpotEntryResponse {
            return SpotEntryResponse(
                id = entity.id ?: 0L,
                lat = entity.spot.lat,
                licensePlate = entity.garageEntry.licensePlate,
                lng = entity.spot.lng,
                occupation = entity.actualOccupation
            )
        }
    }
}

data class GarageOutResponse(
    val id: Long,
    @JsonProperty("exit_time")
    val exitTime: Instant,
    @JsonProperty("license_plate")
    val licensePlate: String,
    @JsonProperty("total_value")
    val totalValue: BigDecimal,
) : WebHookResponse() {
    companion object {
        fun fromEntity(entity: GarageOut): GarageOutResponse {
            return GarageOutResponse(
                id = entity.id ?: 0L,
                exitTime = entity.exitTime,
                licensePlate = entity.garageEntry.licensePlate,
                totalValue = entity.totalValue
            )
        }
    }
}