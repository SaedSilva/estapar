package br.dev.saed.estapar.entities

import jakarta.persistence.*

@Entity(name = "tb_spot")
class Spot(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var lat: Double,
    var lng: Double,
    var occupied: Boolean,

    @ManyToOne
    @JoinColumn(name = "sector", nullable = false, referencedColumnName = "sector")
    var sector: Sector,
) {

}