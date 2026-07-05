package dev.renanbambam.payment.domain.port.inbound

import dev.renanbambam.payment.domain.model.Payment
import dev.renanbambam.payment.domain.model.PaymentEvent

interface PublishPaymentPort {
    fun publish(payment: Payment): PaymentEvent
}
