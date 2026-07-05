package dev.renanbambam.payment.domain.service

import dev.renanbambam.payment.domain.model.Payment
import dev.renanbambam.payment.domain.model.PaymentEvent
import dev.renanbambam.payment.domain.model.PaymentEventType
import dev.renanbambam.payment.domain.port.inbound.PublishPaymentPort
import dev.renanbambam.payment.domain.port.outbound.PaymentEventPublisher
import dev.renanbambam.payment.domain.port.outbound.PaymentRepository
import java.time.Instant
import java.util.UUID

// só depende de interfaces (portas), nunca de implementações
class PaymentPublisherService(
    private val publisher: PaymentEventPublisher,
    private val repository: PaymentRepository
) : PublishPaymentPort {

    override fun publish(payment: Payment): PaymentEvent {
        // persiste antes de publicar: se o banco falhar, nada vai pro tópico
        val saved = repository.save(payment)
        val event = buildEvent(saved)
        publisher.publish(event)
        return event
    }

    private fun buildEvent(payment: Payment) = PaymentEvent(
        eventId = UUID.randomUUID().toString(),
        paymentId = payment.id,
        eventType = PaymentEventType.PAYMENT_CREATED,
        payload = payment,
        occurredAt = Instant.now()
    )
}
