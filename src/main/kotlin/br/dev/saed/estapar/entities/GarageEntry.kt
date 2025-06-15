package br.dev.saed.estapar.entities

import jakarta.persistence.*
import java.time.Instant

@Entity(name = "tb_garage_entry")
class GarageEntry(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var licensePlate: String,

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    var entryTime: Instant,

    @OneToOne(mappedBy = "garageEntry")
    var spotEntry: SpotEntry? = null,

    @OneToOne(mappedBy = "garageEntry")
    var garageOut: GarageOut? = null,
) {

}