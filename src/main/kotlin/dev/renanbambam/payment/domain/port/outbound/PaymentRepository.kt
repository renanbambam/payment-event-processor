package dev.renanbambam.payment.domain.port.outbound

import dev.renanbambam.payment.domain.model.Payment

interface PaymentRepository {
    fun save(payment: Payment): Payment
    fun findById(id: String): Payment?
}
