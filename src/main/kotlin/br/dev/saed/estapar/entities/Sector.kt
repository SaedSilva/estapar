package br.dev.saed.estapar.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import java.math.BigDecimal
import java.time.LocalTime

@Entity(name = "tb_sector")
class Sector(
    @Id
    var sector: String? = null,
    @Column(columnDefinition = "decimal(10, 2)")
    var basePrice: BigDecimal,
    var maxCapacity: Int,
    var openHour: LocalTime,
    var closeHour: LocalTime,
    var durationLimitMinutes: Int,

    @OneToMany(mappedBy = "sector")
    val spots: List<Spot> = emptyList(),
)