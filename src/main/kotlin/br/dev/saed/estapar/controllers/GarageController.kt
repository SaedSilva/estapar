package br.dev.saed.estapar.controllers

import br.dev.saed.estapar.dtos.request.LicensePlateStatusRequest
import br.dev.saed.estapar.dtos.request.RevenueRequest
import br.dev.saed.estapar.dtos.request.SpotStatusRequest
import br.dev.saed.estapar.dtos.request.WebHookRequest
import br.dev.saed.estapar.dtos.response.LicensePlateStatusResponse
import br.dev.saed.estapar.dtos.response.RevenueResponse
import br.dev.saed.estapar.dtos.response.SpotStatusResponse
import br.dev.saed.estapar.dtos.response.WebHookResponse
import br.dev.saed.estapar.services.GarageService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller for handling garage-related operations.
 */

@RestController
@RequestMapping
class GarageController(
    private val service: GarageService
) {


    @PostMapping("/webhook")
    suspend fun events(@RequestBody body: WebHookRequest): ResponseEntity<WebHookResponse> {
        println(body)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(service.handleEvent(body))
    }

    @PostMapping("/plate-status")
    suspend fun plateStatus(@RequestBody body: LicensePlateStatusRequest): ResponseEntity<LicensePlateStatusResponse> {
        println(body)
        return ResponseEntity.ok(service.licensePlateStatus(body))
    }

    @PostMapping("/spot-status")
    suspend fun spotStatus(@RequestBody body: SpotStatusRequest): ResponseEntity<SpotStatusResponse> {
        println(body)
        return ResponseEntity.ok(service.spotStatus(body))
    }

    @GetMapping("/revenue")
    suspend fun revenue(@RequestBody body: RevenueRequest): ResponseEntity<RevenueResponse> {
        println(body)
        return ResponseEntity.ok(service.revenue(body))
    }
}