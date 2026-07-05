package dev.renanbambam.payment.domain.service

import dev.renanbambam.payment.domain.model.Payment
import dev.renanbambam.payment.domain.model.PaymentNotFoundException
import dev.renanbambam.payment.domain.model.PaymentStatus
import dev.renanbambam.payment.domain.port.outbound.PaymentRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.math.BigDecimal
import java.time.Instant

class PaymentQueryServiceTest {

    private val repository: PaymentRepository = mock()
    private val service = PaymentQueryService(repository)

    @Test
    fun `busca pagamento por id`() {
        val payment = samplePayment(PaymentStatus.PENDING)
        whenever(repository.findById("pay-1")).thenReturn(payment)

        assertEquals(payment, service.findById("pay-1"))
    }

    @Test
    fun `retorna null quando nao existe`() {
        whenever(repository.findById("nope")).thenReturn(null)

        assertNull(service.findById("nope"))
    }

    @Test
    fun `atualiza status preservando os demais campos`() {
        val payment = samplePayment(PaymentStatus.PENDING)
        whenever(repository.findById("pay-1")).thenReturn(payment)
        whenever(repository.save(any())).thenAnswer { it.getArgument(0) }

        val updated = service.updateStatus("pay-1", PaymentStatus.PROCESSING)

        assertEquals(PaymentStatus.PROCESSING, updated.status)
        assertEquals(payment.amount, updated.amount)

        val captor = argumentCaptor<Payment>()
        verify(repository).save(captor.capture())
        assertEquals(PaymentStatus.PROCESSING, captor.firstValue.status)
    }

    @Test
    fun `lanca excecao ao atualizar pagamento inexistente`() {
        whenever(repository.findById("nope")).thenReturn(null)

        assertThrows<PaymentNotFoundException> {
            service.updateStatus("nope", PaymentStatus.PROCESSING)
        }
    }

    private fun samplePayment(status: PaymentStatus): Payment {
        val now = Instant.now()
        return Payment(
            id = "pay-1",
            amount = BigDecimal("150.00"),
            currency = "BRL",
            senderId = "user-1",
            receiverId = "user-2",
            status = status,
            createdAt = now,
            updatedAt = now
        )
    }
}
