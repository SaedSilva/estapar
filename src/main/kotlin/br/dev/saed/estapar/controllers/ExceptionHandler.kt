package br.dev.saed.estapar.controllers

import br.dev.saed.estapar.services.execeptions.SectorLimitExceededException
import br.dev.saed.estapar.services.execeptions.SpotOccupiedException
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Global exception handler for the Estapar application.
 */

@RestControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(SectorLimitExceededException::class)
    fun handleSectorLimitExceededException(
        exception: SectorLimitExceededException
    ): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(exception.message ?: "Sector limit exceeded")
    }

    @ExceptionHandler(SpotOccupiedException::class)
    fun handleSpotOccupiedException(
        exception: SpotOccupiedException
    ): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(exception.message ?: "Spot is already occupied")
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(
        exception: EntityNotFoundException
    ): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(exception.message ?: "Entity not found")
    }
}