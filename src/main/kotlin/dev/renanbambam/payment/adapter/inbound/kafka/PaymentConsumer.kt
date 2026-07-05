package dev.renanbambam.payment.adapter.inbound.kafka

import dev.renanbambam.payment.domain.model.PaymentStatus
import dev.renanbambam.payment.domain.port.inbound.QueryPaymentPort
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class PaymentConsumer(
    private val queryPort: QueryPaymentPort,
    private val mapper: PaymentConsumerMapper,
    private val deadLetterHandler: DeadLetterHandler
) {

    private val log = LoggerFactory.getLogger(PaymentConsumer::class.java)

    @KafkaListener(topics = ["\${kafka.topics.payments}"], groupId = "\${kafka.group-id}")
    fun consume(record: ConsumerRecord<String, String>) {
        try {
            val event = mapper.toEvent(record.value())
            log.info("processando evento paymentId={} tipo={}", event.paymentId, event.eventType)
            queryPort.updateStatus(event.paymentId, PaymentStatus.PROCESSING)
        } catch (ex: Exception) {
            deadLetterHandler.send(record.key(), record.value(), ex)
        }
    }
}
