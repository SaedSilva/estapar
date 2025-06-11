package br.dev.saed.estapar.config

import br.dev.saed.estapar.dtos.response.GarageResponse
import br.dev.saed.estapar.services.GarageService
import kotlinx.coroutines.runBlocking
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class StartUp(
    private val client: WebClient,
    private val garageService: GarageService
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
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
    }
}