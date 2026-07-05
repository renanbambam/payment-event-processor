package dev.renanbambam.payment.adapter.inbound.rest.dto

import java.math.BigDecimal
import java.time.Instant

data class PaymentResponse(
    val id: String,
    val amount: BigDecimal,
    val currency: String,
    val senderId: String,
    val receiverId: String,
    val status: String,
    val createdAt: Instant,
    val updatedAt: Instant
)
