package br.dev.saed.estapar.config

import br.dev.saed.estapar.dtos.response.GarageResponse
import br.dev.saed.estapar.services.GarageService
import kotlinx.coroutines.runBlocking
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono


/**
 * This component is responsible for initializing the application by fetching the initial garage data
 * from an external service and setting it up in the local database.
 */

@Component
class StartUp(
    private val client: WebClient,
    private val garageService: GarageService
) : ApplicationRunner {

    /**
     * This method is called at application startup to fetch the initial garage data
     * from the external service and set it up in the local database.
     */
    override fun run(args: ApplicationArguments?) {
        try {
            val dto: GarageResponse? = client
                .get()
                .uri("/garage")
                .retrieve()
                .bodyToMono<GarageResponse>()
                .block()

            dto?.let { dto ->
                runBlocking {
                    garageService.setupInitialData(dto)
                }
            }
        } catch (e: Exception) {
            //TODO Precisa ser tratado de forma mais adequada, mantendo por enquanto para não quebrar a aplicação
            println("Error during startup: ${e.message}")
            e.printStackTrace()
        }
    }
}