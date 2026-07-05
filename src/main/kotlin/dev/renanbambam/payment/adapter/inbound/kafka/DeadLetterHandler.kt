package dev.renanbambam.payment.adapter.inbound.kafka

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

// mensagens que o consumer não consegue processar vão pra cá em vez de travar o tópico principal
@Component
class DeadLetterHandler(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    @Value("\${kafka.topics.payments-dlq}") private val dlqTopic: String
) {

    private val log = LoggerFactory.getLogger(DeadLetterHandler::class.java)

    fun send(key: String?, payload: String, cause: Exception) {
        log.error("mensagem enviada pra DLQ topic={} motivo={}", dlqTopic, cause.message, cause)
        // key pode vir nula quando a mensagem original não tinha chave
        if (key == null) {
            kafkaTemplate.send(dlqTopic, payload)
        } else {
            kafkaTemplate.send(dlqTopic, key, payload)
        }
    }
}
