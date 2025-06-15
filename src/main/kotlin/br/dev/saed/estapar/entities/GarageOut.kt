package br.dev.saed.estapar.entities

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant

@Entity(name = "tb_garage_out")
class GarageOut(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    var exitTime: Instant,

    @Column(columnDefinition = "DECIMAL(10, 2)")
    var totalValue: BigDecimal,

    @OneToOne
    @JoinColumn(name = "garage_entry_id", referencedColumnName = "id")
    var garageEntry: GarageEntry,

    @OneToOne
    @JoinColumn(name = "spot_entry_id", referencedColumnName = "id")
    var spotEntry: SpotEntry? = null,
) {

}