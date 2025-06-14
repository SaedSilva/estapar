package br.dev.saed.estapar.dtos.response

import java.math.BigDecimal
import java.time.Instant

data class RevenueResponse(
    val amount: BigDecimal,
    val currency: String,
    val timestamp: Instant,
)
