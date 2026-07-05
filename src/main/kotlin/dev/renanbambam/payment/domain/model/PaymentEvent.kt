package dev.renanbambam.payment.domain.model

import java.time.Instant

// evento publicado no Kafka — imutável
data class PaymentEvent(
    val eventId: String,
    val paymentId: String,
    val eventType: PaymentEventType,
    val payload: Payment,
    val occurredAt: Instant
)

enum class PaymentEventType {
    PAYMENT_CREATED,
    PAYMENT_PROCESSING,
    PAYMENT_COMPLETED,
    PAYMENT_FAILED
}
