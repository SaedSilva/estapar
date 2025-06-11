package br.dev.saed.estapar.dtos.request

import com.fasterxml.jackson.annotation.JsonProperty

data class LicensePlateStatusRequest(
    @JsonProperty("license_plate")
    val licensePlate: String
)
