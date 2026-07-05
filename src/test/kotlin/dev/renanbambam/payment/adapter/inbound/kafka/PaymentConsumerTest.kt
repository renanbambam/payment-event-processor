package dev.renanbambam.payment.adapter.inbound.kafka

import dev.renanbambam.payment.domain.model.Payment
import dev.renanbambam.payment.domain.model.PaymentEvent
import dev.renanbambam.payment.domain.model.PaymentEventType
import dev.renanbambam.payment.domain.model.PaymentStatus
import dev.renanbambam.payment.domain.port.inbound.QueryPaymentPort
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.time.Instant

class PaymentConsumerTest {

    private val queryPort: QueryPaymentPort = mock()
    private val mapper: PaymentConsumerMapper = mock()
    private val deadLetterHandler: DeadLetterHandler = mock()
    private val consumer = PaymentConsumer(queryPort, mapper, deadLetterHandler)

    @Test
    fun `atualiza status para processing ao consumir evento valido`() {
        val record = record("pay-1", "{...}")
        whenever(mapper.toEvent("{...}")).thenReturn(sampleEvent("pay-1"))

        consumer.consume(record)

        verify(queryPort).updateStatus("pay-1", PaymentStatus.PROCESSING)
        verify(deadLetterHandler, never()).send(any(), any(), any())
    }

    @Test
    fun `envia para DLQ quando o payload nao pode ser lido`() {
        val record = record("pay-1", "payload-quebrado")
        whenever(mapper.toEvent("payload-quebrado")).thenThrow(RuntimeException("json inválido"))

        consumer.consume(record)

        verify(deadLetterHandler).send(eq("pay-1"), eq("payload-quebrado"), any())
        verify(queryPort, never()).updateStatus(any(), any())
    }

    private fun record(key: String, value: String) =
        ConsumerRecord("payments.events", 0, 0L, key, value)

    private fun sampleEvent(paymentId: String): PaymentEvent {
        val now = Instant.now()
        val payment = Payment(
            id = paymentId,
            amount = BigDecimal("150.00"),
            currency = "BRL",
            senderId = "user-1",
            receiverId = "user-2",
            status = PaymentStatus.PENDING,
            createdAt = now,
            updatedAt = now
        )
        return PaymentEvent(
            eventId = "evt-1",
            paymentId = paymentId,
            eventType = PaymentEventType.PAYMENT_CREATED,
            payload = payment,
            occurredAt = now
        )
    }
}
