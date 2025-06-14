package br.dev.saed.estapar.dtos.request

import java.time.LocalDate

data class RevenueRequest(
    val date: LocalDate,
    val sector: String,
)
