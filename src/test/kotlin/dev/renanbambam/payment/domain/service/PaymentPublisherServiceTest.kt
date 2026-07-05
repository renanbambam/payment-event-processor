package dev.renanbambam.payment.domain.service

import dev.renanbambam.payment.domain.model.Payment
import dev.renanbambam.payment.domain.model.PaymentEvent
import dev.renanbambam.payment.domain.model.PaymentEventType
import dev.renanbambam.payment.domain.model.PaymentStatus
import dev.renanbambam.payment.domain.port.outbound.PaymentEventPublisher
import dev.renanbambam.payment.domain.port.outbound.PaymentRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.time.Instant

class PaymentPublisherServiceTest {

    private val publisher: PaymentEventPublisher = mock()
    private val repository: PaymentRepository = mock()
    private val service = PaymentPublisherService(publisher, repository)

    @Test
    fun `persiste e publica evento de criacao`() {
        val payment = samplePayment()
        whenever(repository.save(payment)).thenReturn(payment)

        val event = service.publish(payment)

        assertEquals(payment.id, event.paymentId)
        assertEquals(PaymentEventType.PAYMENT_CREATED, event.eventType)
        assertEquals(payment, event.payload)

        val captor = argumentCaptor<PaymentEvent>()
        verify(publisher).publish(captor.capture())
        assertEquals(event, captor.firstValue)
    }

    @Test
    fun `persiste antes de publicar`() {
        val payment = samplePayment()
        whenever(repository.save(any())).thenReturn(payment)

        service.publish(payment)

        // se publicasse antes de salvar, um erro no banco vazaria evento fantasma
        inOrder(repository, publisher) {
            verify(repository).save(payment)
            verify(publisher).publish(any())
        }
    }

    private fun samplePayment(): Payment {
        val now = Instant.now()
        return Payment(
            id = "pay-1",
            amount = BigDecimal("150.00"),
            currency = "BRL",
            senderId = "user-1",
            receiverId = "user-2",
            status = PaymentStatus.PENDING,
            createdAt = now,
            updatedAt = now
        )
    }
}
