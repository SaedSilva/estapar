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
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset

@Service
class GarageService(
    private val sectorRepository: SectorRepository,
    private val spotRepository: SpotRepository,
    private val garageEntryRepository: GarageEntryRepository,
    private val spotEntryRepository: SpotEntryRepository,
    private val garageOutRepository: GarageOutRepository,
    private val garageCalculatorService: GarageCalculatorService,
) {

    @Transactional
    suspend fun setupInitialData(dto: GarageResponse) {
        val sectors = dto.garage.map { response ->
            response.toEntity()
        }
        sectorRepository.saveAll(sectors)

        val spots = dto.spots.map { response ->
            response.toEntity()
        }
        spotRepository.saveAll(spots)
    }


    suspend fun handleEvent(webHookRequest: WebHookRequest): WebHookResponse {
        return when (webHookRequest) {
            is GarageEntryRequest -> garageEntry(webHookRequest)
            is SpotEntryRequest -> spotEntry(webHookRequest)
            is GarageOutRequest -> garageOut(webHookRequest)
        }
    }


    @Transactional
    internal suspend fun garageEntry(request: GarageEntryRequest): GarageEntryResponse {
        val existingEntry = garageEntryRepository.findGarageEntryByLicensePlateAndGarageOutIsNull(request.licensePlate)
        if (existingEntry != null) {
            throw EntityNotFoundException("Garage entry already exists for license plate ${request.licensePlate}")
        }
        val entry = garageEntryRepository.save(request.toEntity())
        return GarageEntryResponse.fromEntity(entry)
    }

    @Transactional
    internal suspend fun spotEntry(request: SpotEntryRequest): SpotEntryResponse {
        val spot = spotRepository.findSpotByLatAndLng(request.lat, request.lng)
            ?: throw EntityNotFoundException("Spot not found at coordinates (${request.lat}, ${request.lng})")

        if (spot.occupied) {
            throw SpotOccupiedException("Spot at coordinates (${request.lat}, ${request.lng}) is already occupied")
        }

        val occupation = sectorRepository.getActualOccupation(
            spot.sector.sector
                ?: throw EntityNotFoundException("Sector not found for spot at coordinates (${request.lat}, ${request.lng})")
        )

        if (occupation >= 100.0f) {
            throw SectorLimitExceededException("Park lotted :)")
        }

        val garageEntry = garageEntryRepository.findGarageEntryByLicensePlateAndGarageOutIsNull(request.licensePlate)
            ?: throw EntityNotFoundException("Garage entry not found for license plate ${request.licensePlate}")

        val spotEntry = SpotEntry(
            actualOccupation = occupation,
            spot = spot,
            garageEntry = garageEntry,
        )

        spot.occupied = true

        spotRepository.save(spot)
        return SpotEntryResponse.fromEntity(spotEntryRepository.save(spotEntry))
    }

    @Transactional
    internal suspend fun garageOut(request: GarageOutRequest): GarageOutResponse {
        val spotEntry =
            spotEntryRepository.findByGarageEntryLicensePlateAndGarageOutIsNull(request.licensePlate)
        val exitTime = request.exitTime.toInstant(ZoneOffset.UTC)

        if (spotEntry == null) {
            val garageEntry =
                garageEntryRepository.findGarageEntryByLicensePlateAndGarageOutIsNull(request.licensePlate)
                    ?: throw EntityNotFoundException("Garage entry not found for license plate ${request.licensePlate}")
            val garageOut = GarageOut(
                exitTime = exitTime,
                totalValue = BigDecimal.ZERO,
                garageEntry = garageEntry,
                spotEntry = null,
            )
            return GarageOutResponse.fromEntity(garageOutRepository.save(garageOut))
        }


        val calculatedValue = garageCalculatorService.calculateValue(
            occupation = spotEntry.actualOccupation,
            basePrice = spotEntry.spot.sector.basePrice,
            entryTime = spotEntry.garageEntry.entryTime,
            outTime = exitTime
        )

        val garageOut = GarageOut(
            exitTime = exitTime,
            totalValue = calculatedValue,
            garageEntry = spotEntry.garageEntry,
            spotEntry = spotEntry,
        )

        val spot = spotEntry.spot
        spot.occupied = false
        spotRepository.save(spot)
        return GarageOutResponse.fromEntity(garageOutRepository.save(garageOut))
    }

    @Transactional(readOnly = true)
    suspend fun licensePlateStatus(request: LicensePlateStatusRequest): LicensePlateStatusResponse {
        val garageEntry = garageEntryRepository.findGarageEntryByLicensePlateAndGarageOutIsNull(request.licensePlate)
            ?: throw EntityNotFoundException("Garage entry not found for license plate ${request.licensePlate}")

        val spotEntry = garageEntry.spotEntry
            ?: throw EntityNotFoundException("Spot entry not found for license plate ${request.licensePlate}")

        val now = Instant.now()

        val actualValue = garageCalculatorService.calculateValue(
            occupation = spotEntry.actualOccupation,
            basePrice = spotEntry.spot.sector.basePrice,
            entryTime = spotEntry.garageEntry.entryTime,
            outTime = now
        )

        val timeParked = Duration.between(spotEntry.garageEntry.entryTime, now)
        val timeParkedFormatted = "${timeParked.toHours()}:${timeParked.toMinutesPart()}:${timeParked.toSecondsPart()}"

        return LicensePlateStatusResponse(
            licensePlate = garageEntry.licensePlate,
            priceUntilNow = actualValue,
            entryTime = garageEntry.entryTime,
            timeParked = timeParkedFormatted,
            lat = spotEntry.spot.lat,
            lng = spotEntry.spot.lng,
        )
    }

    @Transactional(readOnly = true)
    suspend fun spotStatus(request: SpotStatusRequest): SpotStatusResponse {
        val spot = spotRepository.findSpotByLatAndLng(request.lat, request.lng)
            ?: throw EntityNotFoundException("Spot not found at coordinates (${request.lat}, ${request.lng})")

        if (!spot.occupied) {
            return SpotStatusResponse(
                occupied = false,
                licensePlate = null,
                priceUntilNow = BigDecimal.ZERO,
                entryTime = null,
                timeParked = null,
            )
        }

        val spotId = spot.id
            ?: throw EntityNotFoundException("Spot ID not found for coordinates (${request.lat}, ${request.lng})")
        val spotEntry = spotEntryRepository.findBySpotIdAndGarageOutIsNull(spotId)
            ?: throw EntityNotFoundException("Spot entry not found for spot ID $spotId")
        val garageEntry = spotEntry.garageEntry

        val now = Instant.now()

        val price = garageCalculatorService.calculateValue(
            occupation = spotEntry.actualOccupation,
            basePrice = spot.sector.basePrice,
            entryTime = garageEntry.entryTime,
            outTime = now,
        )

        val timeParked = Duration.between(spotEntry.garageEntry.entryTime, now)
        val timeParkedFormatted = "${timeParked.toHours()}:${timeParked.toMinutesPart()}:${timeParked.toSecondsPart()}"

        return SpotStatusResponse(
            occupied = true,
            licensePlate = garageEntry.licensePlate,
            priceUntilNow = price,
            entryTime = garageEntry.entryTime,
            timeParked = timeParkedFormatted
        )
    }

    @Transactional(readOnly = true)
    suspend fun revenue(body: RevenueRequest): RevenueResponse {
        val total = garageOutRepository.findTotalValueBySectorAndExitDate(
            sector = body.sector,
            date = body.date
        ) ?: BigDecimal("0.00")

        return RevenueResponse(
            amount = total,
            currency = "BRL", // TODO em nenhum lugar antes foi definido o tipo de moeda
            timestamp = Instant.now(),
        )
    }
}
