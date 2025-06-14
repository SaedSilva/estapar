package br.dev.saed.estapar.entities

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant

@Entity(name = "tb_garage_out")
class GarageOut(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    val exitTime: Instant,

    @Column(columnDefinition = "DECIMAL(10, 2)")
    val value: BigDecimal,

    @OneToOne
    @JoinColumn(name = "garage_entry_id", referencedColumnName = "id")
    val garageEntry: GarageEntry,

    @OneToOne
    @JoinColumn(name = "spot_entry_id", referencedColumnName = "id")
    val spotEntry: SpotEntry? = null,
) {

}