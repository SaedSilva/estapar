package br.dev.saed.estapar

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EstaparApplication

fun main(args: Array<String>) {
    runApplication<EstaparApplication>(*args)
}
