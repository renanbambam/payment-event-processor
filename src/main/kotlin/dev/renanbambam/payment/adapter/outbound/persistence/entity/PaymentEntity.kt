package dev.renanbambam.payment.adapter.outbound.persistence.entity

import dev.renanbambam.payment.domain.model.PaymentStatus
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "payments")
class PaymentEntity(
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    var id: String,

    @Column(name = "amount", nullable = false)
    var amount: BigDecimal,

    @Column(name = "currency", nullable = false, length = 3)
    var currency: String,

    @Column(name = "sender_id", nullable = false)
    var senderId: String,

    @Column(name = "receiver_id", nullable = false)
    var receiverId: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: PaymentStatus,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant
) {
    // JPA exige construtor sem argumentos
    protected constructor() : this(
        id = "",
        amount = BigDecimal.ZERO,
        currency = "",
        senderId = "",
        receiverId = "",
        status = PaymentStatus.PENDING,
        createdAt = Instant.EPOCH,
        updatedAt = Instant.EPOCH
    )
}
