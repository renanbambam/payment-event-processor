package dev.renanbambam.payment.domain.model

import java.math.BigDecimal
import java.time.Instant

// entidade imutável — sem anotações de framework aqui
data class Payment(
    val id: String,
    val amount: BigDecimal,
    val currency: String,
    val senderId: String,
    val receiverId: String,
    val status: PaymentStatus,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    fun withStatus(newStatus: PaymentStatus, at: Instant = Instant.now()): Payment =
        copy(status = newStatus, updatedAt = at)
}
