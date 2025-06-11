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
    val sector: String? = null,
    @Column(columnDefinition = "decimal(10, 4)")
    val basePrice: BigDecimal,
    val maxCapacity: Int,
    val openHour: LocalTime,
    val closeHour: LocalTime,
    val durationLimitMinutes: Int,

    @OneToMany(mappedBy = "sector")
    val spots: List<Spot> = emptyList(),
) {

}