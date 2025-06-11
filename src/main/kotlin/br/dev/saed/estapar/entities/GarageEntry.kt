package br.dev.saed.estapar.entities

import jakarta.persistence.*
import java.time.Instant

@Entity(name = "tb_garage_entry")
class GarageEntry(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val licensePlate: String,

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    val entryTime: Instant,

    @OneToOne(mappedBy = "garageEntry")
    val spotEntry: SpotEntry? = null,

    @OneToOne(mappedBy = "garageEntry")
    val garageOut: GarageOut? = null,
) {

}