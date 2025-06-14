package br.dev.saed.estapar.entities

import jakarta.persistence.*

@Entity(name = "tb_spot_entry")
class SpotEntry(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val actualOccupation: Float,

    @ManyToOne
    @JoinColumn(name = "spot_id", referencedColumnName = "id", unique = false)
    val spot: Spot,

    @OneToOne
    @JoinColumn(name = "garage_entry_id", referencedColumnName = "id")
    val garageEntry: GarageEntry,

    @OneToOne(mappedBy = "spotEntry")
    val garageOut: GarageOut? = null,
) {

}