package dev.renanbambam.payment.domain.service

import dev.renanbambam.payment.domain.model.Payment
import dev.renanbambam.payment.domain.model.PaymentNotFoundException
import dev.renanbambam.payment.domain.model.PaymentStatus
import dev.renanbambam.payment.domain.port.inbound.QueryPaymentPort
import dev.renanbambam.payment.domain.port.outbound.PaymentRepository

class PaymentQueryService(
    private val repository: PaymentRepository
) : QueryPaymentPort {

    override fun findById(id: String): Payment? = repository.findById(id)

    override fun updateStatus(id: String, status: PaymentStatus): Payment {
        val current = repository.findById(id) ?: throw PaymentNotFoundException(id)
        return repository.save(current.withStatus(status))
    }
}
