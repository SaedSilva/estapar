package br.dev.saed.estapar.dtos.request

import br.dev.saed.estapar.entities.GarageEntry
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import java.time.LocalDateTime
import java.time.ZoneOffset


/**
 * Represents a request for a webhook event.
 *
 * This sealed class is used to handle different types of webhook events
 * such as garage entry, spot entry, and garage exit.
 *
 * @see GarageEntryRequest
 * @see SpotEntryRequest
 * @see GarageOutRequest
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "event_type", include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes(
    value = [
        JsonSubTypes.Type(value = GarageEntryRequest::class, name = "ENTRY"),
        JsonSubTypes.Type(value = SpotEntryRequest::class, name = "PARKED"),
        JsonSubTypes.Type(value = GarageOutRequest::class, name = "EXIT"),
    ]
)
sealed class WebHookRequest

@JsonTypeName("ENTRY")
data class GarageEntryRequest(
    @JsonProperty("entry_time")
    val entryTime: LocalDateTime,
    @JsonProperty("license_plate")
    val licensePlate: String,
) : WebHookRequest()

fun GarageEntryRequest.toEntity() = GarageEntry(
    entryTime = entryTime.toInstant(ZoneOffset.UTC),
    licensePlate = licensePlate
)

@JsonTypeName
data class SpotEntryRequest(
    val lat: Double,
    @JsonProperty("license_plate")
    val licensePlate: String,
    val lng: Double,
) : WebHookRequest()

@JsonTypeName("EXIT")
data class GarageOutRequest(
    @JsonProperty("exit_time")
    val exitTime: LocalDateTime,
    @JsonProperty("license_plate")
    val licensePlate: String,
) : WebHookRequest()