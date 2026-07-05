package dev.renanbambam.payment.adapter.outbound.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import dev.renanbambam.payment.domain.model.PaymentEvent
import org.springframework.stereotype.Component

// isola a serialização do evento pra não vazar Jackson pro publisher
@Component
class PaymentEventSerializer(
    private val objectMapper: ObjectMapper
) {
    fun serialize(event: PaymentEvent): String = objectMapper.writeValueAsString(event)
}
