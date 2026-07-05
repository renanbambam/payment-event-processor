package dev.renanbambam.payment.adapter.inbound.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import dev.renanbambam.payment.domain.model.PaymentEvent
import org.springframework.stereotype.Component

@Component
class PaymentConsumerMapper(
    private val objectMapper: ObjectMapper
) {
    fun toEvent(raw: String): PaymentEvent = objectMapper.readValue(raw, PaymentEvent::class.java)
}
