package br.dev.saed.estapar.entities

import jakarta.persistence.*

@Entity(name = "tb_spot_entry")
class SpotEntry(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var actualOccupation: Float,

    @ManyToOne
    @JoinColumn(name = "spot_id", referencedColumnName = "id", unique = false)
    var spot: Spot,

    @OneToOne
    @JoinColumn(name = "garage_entry_id", referencedColumnName = "id")
    var garageEntry: GarageEntry,

    @OneToOne(mappedBy = "spotEntry")
    var garageOut: GarageOut? = null,
) {

}