package br.dev.saed.estapar.services

import br.dev.saed.estapar.dtos.request.*
import br.dev.saed.estapar.dtos.response.*
import br.dev.saed.estapar.entities.GarageOut
import br.dev.saed.estapar.entities.Spot
import br.dev.saed.estapar.entities.SpotEntry
import br.dev.saed.estapar.repositories.*
import br.dev.saed.estapar.services.execeptions.SectorLimitExceededException
import br.dev.saed.estapar.services.execeptions.SpotOccupiedException
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class GarageService(
    private val sectorRepository: SectorRepository,
    private val spotRepository: SpotRepository,
    private val garageEntryRepository: GarageEntryRepository,
    private val spotEntryRepository: SpotEntryRepository,
    private val garageOutRepository: GarageOutRepository,
) {

    @Transactional
    suspend fun setupInitialData(dto: GarageResponse) {
        withContext(Dispatchers.IO) {
            val sectors = dto.garage.map { response ->
                response.toEntity()
            }
            sectorRepository.saveAll(sectors)

            val spots = dto.spots.map { response ->
                response.toEntity()
            }
            spotRepository.saveAll(spots)
        }
    }

    @Transactional
    suspend fun handleEvent(webHookRequest: WebHookRequest): WebHookResponse {
        return withContext(Dispatchers.IO) {
            when (webHookRequest) {
                is GarageEntryRequest -> garageEntry(webHookRequest)
                is SpotEntryRequest -> spotEntry(webHookRequest)
                is GarageOutRequest -> garageOut(webHookRequest)
            }
        }
    }

    private fun garageEntry(request: GarageEntryRequest): GarageEntryResponse {
        return GarageEntryResponse.fromEntity(garageEntryRepository.save(request.toEntity()))
    }

    private fun spotEntry(request: SpotEntryRequest): SpotEntryResponse {
        val spot = spotRepository.findSpotByLatAndLng(request.lat, request.lng)
            ?: throw EntityNotFoundException("Spot not found at coordinates (${request.lat}, ${request.lng})")

        if (spot.occupied) {
            throw SpotOccupiedException("Spot at coordinates (${request.lat}, ${request.lng}) is already occupied")
        }

        val occupation = sectorRepository.getActualOccupation(spot.sector.sector ?: "")

        if (occupation >= 100.0f) {
            throw SectorLimitExceededException("Park lotted :)")
        }

        val garageEntry = garageEntryRepository.findGarageEntryByLicensePlateAndGarageOutIsNull(request.licensePlate)
            ?: throw EntityNotFoundException("Garage entry not found for license plate ${request.licensePlate}")

        val spotEntry = SpotEntry(
            actualOccupation = occupation,
            timeParked = Instant.now(),
            spot = spot,
            garageEntry = garageEntry,
        )

        val updatedSpot = Spot(
            id = spot.id,
            lat = spot.lat,
            lng = spot.lng,
            occupied = true,
            sector = spot.sector,
        )

        spotRepository.save(updatedSpot)
        return SpotEntryResponse.fromEntity(spotEntryRepository.save(spotEntry))
    }

    private fun garageOut(request: GarageOutRequest): GarageOutResponse {
        val spotEntry =
            spotEntryRepository.findByGarageEntryLicensePlateAndGarageOutIsNull(request.licensePlate)
        val exitTime = LocalDateTime.parse(request.exitTime).toInstant(ZoneOffset.UTC)

        if (spotEntry == null) {
            val garageEntry =
                garageEntryRepository.findGarageEntryByLicensePlateAndGarageOutIsNull(request.licensePlate)
                    ?: throw EntityNotFoundException("Garage entry not found for license plate ${request.licensePlate}")
            val garageOut = GarageOut(
                exitTime = exitTime,
                value = BigDecimal.ZERO,
                garageEntry = garageEntry,
                spotEntry = null,
            )
            return GarageOutResponse.fromEntity(garageOutRepository.save(garageOut))
        }


        val calculatedValue = calculateValue(spotEntry, exitTime)

        val garageOut = GarageOut(
            exitTime = exitTime,
            value = calculatedValue,
            garageEntry = spotEntry.garageEntry,
            spotEntry = spotEntry,
        )

        val spot = spotEntry.spot
        val updatedSpot = Spot(
            id = spot.id,
            lat = spot.lat,
            lng = spot.lng,
            occupied = false,
            sector = spot.sector,
        )
        spotRepository.save(updatedSpot)

        return GarageOutResponse.fromEntity(garageOutRepository.save(garageOut))
    }

    fun licensePlateStatus(request: LicensePlateStatusRequest): LicensePlateStatusResponse {
        val garageEntry = garageEntryRepository.findGarageEntryByLicensePlateAndGarageOutIsNull(request.licensePlate)
            ?: throw EntityNotFoundException("Garage entry not found for license plate ${request.licensePlate}")

        val spotEntry = garageEntry.spotEntry
            ?: throw EntityNotFoundException("Spot entry not found for license plate ${request.licensePlate}")

        val now = Instant.now()

        val actualValue = calculateValue(spotEntry, now)

        val timeParked = Duration.between(spotEntry.garageEntry.entryTime, now).toMinutes()

        return LicensePlateStatusResponse(
            licensePlate = garageEntry.licensePlate,
            priceUntilNow = actualValue,
            entryTime = garageEntry.entryTime,
            timeParked = spotEntry.timeParked,
            lat = spotEntry.spot.lat,
            lng = spotEntry.spot.lng,
        )
    }

    private fun calculateValue(entry: SpotEntry, time: Instant): BigDecimal {
        val occupation = entry.actualOccupation
        val basePrice = entry.spot.sector.basePrice
        val entryTime = entry.garageEntry.entryTime
        val hoursFloat = (time.toEpochMilli() - entryTime.toEpochMilli()).toFloat() / (1000 * 60 * 60)
        val totalPrice = basePrice.multiply(BigDecimal.valueOf(hoursFloat.toDouble()))
        return applyDynamicPrice(totalPrice, occupation)
    }

    private fun applyDynamicPrice(value: BigDecimal, occupation: Float): BigDecimal {
        return when {
            occupation < 0.25f -> {
                value.multiply(BigDecimal("0.9"))
            }

            occupation < 0.50f -> {
                value
            }

            occupation < 0.75f -> {
                value.multiply(BigDecimal("1.10"))
            }

            occupation <= 100.0f -> {
                value.multiply(BigDecimal("1.25"))
            }

            else -> {
                throw IllegalArgumentException("Occupation percentage must be between 0 and 100")
            }
        }
    }
}