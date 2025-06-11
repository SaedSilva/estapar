package br.dev.saed.estapar.controllers

import br.dev.saed.estapar.services.execeptions.SectorLimitExceededException
import br.dev.saed.estapar.services.execeptions.SpotOccupiedException
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(SectorLimitExceededException::class)
    fun handleSectorLimitExceededException(
        exception: SectorLimitExceededException,
        request: WebRequest
    ): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(exception.message ?: "Sector limit exceeded")
    }

    @ExceptionHandler(SpotOccupiedException::class)
    fun handleSpotOccupiedException(
        exception: SpotOccupiedException,
        request: WebRequest
    ): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(exception.message ?: "Spot is already occupied")
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(
        exception: EntityNotFoundException,
        request: WebRequest
    ): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(exception.message ?: "Entity not found")
    }
}