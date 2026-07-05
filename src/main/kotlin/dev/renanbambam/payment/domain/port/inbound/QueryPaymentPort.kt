package dev.renanbambam.payment.domain.port.inbound

import dev.renanbambam.payment.domain.model.Payment
import dev.renanbambam.payment.domain.model.PaymentStatus

interface QueryPaymentPort {
    fun findById(id: String): Payment?
    fun updateStatus(id: String, status: PaymentStatus): Payment
}
