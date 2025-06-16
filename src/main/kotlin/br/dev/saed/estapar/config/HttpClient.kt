package br.dev.saed.estapar.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import org.springframework.web.reactive.function.client.WebClient

/**
 * Configuration class for setting up the WebClient used to interact with external services.
 * This client is configured to connect to a local server running on port 3000.
 */

@Configuration
class HttpClient {

    /**
     * Creates a WebClient bean configured with the base URL for the external service.
     * This client can be used to make HTTP requests to the specified base URL.
     *
     * @return A configured WebClient instance.
     */
    @Bean
    @Profile("prod")
    fun webClient() = WebClient.builder()
        .baseUrl("http://localhost:3000")
        .build()
}