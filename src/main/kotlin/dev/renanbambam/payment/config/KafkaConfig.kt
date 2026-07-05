package dev.renanbambam.payment.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
class KafkaConfig(
    @Value("\${kafka.topics.payments}") private val paymentsTopic: String,
    @Value("\${kafka.topics.payments-dlq}") private val dlqTopic: String
) {

    @Bean
    fun paymentsTopic(): NewTopic =
        TopicBuilder.name(paymentsTopic).partitions(3).replicas(1).build()

    // DLQ com uma partição só: volume esperado é baixo, não precisa paralelizar
    @Bean
    fun paymentsDlqTopic(): NewTopic =
        TopicBuilder.name(dlqTopic).partitions(1).replicas(1).build()
}
