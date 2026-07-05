package dev.renanbambam.payment.adapter.outbound.kafka

import dev.renanbambam.payment.domain.model.PaymentEvent
import dev.renanbambam.payment.domain.port.outbound.PaymentEventPublisher
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaPaymentPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val serializer: PaymentEventSerializer,
    @Value("\${kafka.topics.payments}") private val topic: String
) : PaymentEventPublisher {

    override fun publish(event: PaymentEvent) {
        val payload = serializer.serialize(event)
        // a key é o paymentId: garante ordem por pagamento dentro da partição
        kafkaTemplate.send(topic, event.paymentId, payload)
            .whenComplete { _, ex ->
                if (ex != null) throw PublishException("falha ao publicar evento", ex)
            }
    }
}
