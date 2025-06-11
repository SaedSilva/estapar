package br.dev.saed.estapar.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class HttpClient {

    @Bean
    fun webClient() = WebClient.builder()
        .baseUrl("http://localhost:3000")
        .build()
}