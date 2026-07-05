package dev.renanbambam.payment.domain.port.outbound

import dev.renanbambam.payment.domain.model.PaymentEvent

interface PaymentEventPublisher {
    fun publish(event: PaymentEvent)
}
