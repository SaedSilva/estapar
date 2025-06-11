package br.dev.saed.estapar.entities

import jakarta.persistence.*

@Entity(name = "tb_spot")
class Spot(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val lat: Double,
    val lng: Double,
    val occupied: Boolean,

    @ManyToOne
    @JoinColumn(name = "sector", nullable = false, referencedColumnName = "sector")
    val sector: Sector,
) {

}