package dev.renanbambam.payment.adapter.inbound.rest.mapper

import dev.renanbambam.payment.adapter.inbound.rest.dto.PaymentRequest
import dev.renanbambam.payment.adapter.inbound.rest.dto.PaymentResponse
import dev.renanbambam.payment.domain.model.Payment
import dev.renanbambam.payment.domain.model.PaymentEvent
import dev.renanbambam.payment.domain.model.PaymentStatus
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID

@Component
class PaymentRestMapper {

    fun toDomain(request: PaymentRequest): Payment {
        val now = Instant.now()
        return Payment(
            id = UUID.randomUUID().toString(),
            amount = request.amount!!,
            currency = request.currency!!.uppercase(),
            senderId = request.senderId!!,
            receiverId = request.receiverId!!,
            status = PaymentStatus.PENDING,
            createdAt = now,
            updatedAt = now
        )
    }

    fun toResponse(payment: Payment): PaymentResponse = PaymentResponse(
        id = payment.id,
        amount = payment.amount,
        currency = payment.currency,
        senderId = payment.senderId,
        receiverId = payment.receiverId,
        status = payment.status.name,
        createdAt = payment.createdAt,
        updatedAt = payment.updatedAt
    )

    fun toResponse(event: PaymentEvent): PaymentResponse = toResponse(event.payload)
}
