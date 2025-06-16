package br.dev.saed.estapar.controllers

import br.dev.saed.estapar.dtos.request.LicensePlateStatusRequest
import br.dev.saed.estapar.dtos.request.RevenueRequest
import br.dev.saed.estapar.dtos.request.SpotStatusRequest
import br.dev.saed.estapar.dtos.request.WebHookRequest
import br.dev.saed.estapar.dtos.response.GarageEntryResponse
import br.dev.saed.estapar.dtos.response.GarageOutResponse
import br.dev.saed.estapar.dtos.response.LicensePlateStatusResponse
import br.dev.saed.estapar.dtos.response.RevenueResponse
import br.dev.saed.estapar.dtos.response.SpotEntryResponse
import br.dev.saed.estapar.dtos.response.SpotStatusResponse
import br.dev.saed.estapar.dtos.response.WebHookResponse
import br.dev.saed.estapar.services.GarageService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
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

    @Operation(summary = "Handle webhook events from Estapar")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Event processed successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "GarageEntryResponse",
                                value = """
                                    {
                                        "licensePlate": "ABC1234",
                                        "entryTime": "2023-10-01T12:00:00Z"
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "SpotEntryResponse",
                                value = """
                                    {
                                        "licensePlate": "ABC1234",
                                        "spotId": 1,
                                        "entryTime": "2023-10-01T12:05:00Z"
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "GarageOutResponse",
                                value = """
                                    {
                                        "licensePlate": "ABC1234",
                                        "exitTime": "2023-10-01T14:00:00Z",
                                        "totalAmount": 20.00
                                    }
                                """
                            )
                        ],
                        schema = Schema(
                            oneOf = [
                                GarageEntryResponse::class,
                                SpotEntryResponse::class,
                                GarageOutResponse::class
                            ]
                        )
                    )
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad Request - Invalid event data"
            )
        ]
    )
    @PostMapping("/webhook")
    suspend fun events(@RequestBody body: WebHookRequest): ResponseEntity<WebHookResponse> {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(service.handleEvent(body))
    }

    @Operation(summary = "Get the status of a license plate")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "License plate status retrieved successfully"
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad Request - Invalid license plate data"
            ),
            ApiResponse(
                responseCode = "404",
                description = "License plate not found"
            ),
        ]
    )
    @PostMapping("/plate-status")
    suspend fun plateStatus(@RequestBody body: LicensePlateStatusRequest): ResponseEntity<LicensePlateStatusResponse> {
        return ResponseEntity.ok(service.licensePlateStatus(body))
    }

    @Operation(summary = "Get the status of a parking spot")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Spot status retrieved successfully"
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad Request - Invalid spot data"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Spot not found"
            ),
        ]
    )
    @PostMapping("/spot-status")
    suspend fun spotStatus(@RequestBody body: SpotStatusRequest): ResponseEntity<SpotStatusResponse> {
        return ResponseEntity.ok(service.spotStatus(body))
    }

    @Operation(summary = "Get the revenue for a specific period")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Revenue data retrieved successfully"
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad Request - Invalid revenue request data"
            ),
        ]
    )
    @GetMapping("/revenue")
    suspend fun revenue(@RequestBody body: RevenueRequest): ResponseEntity<RevenueResponse> {
        return ResponseEntity.ok(service.revenue(body))
    }

}