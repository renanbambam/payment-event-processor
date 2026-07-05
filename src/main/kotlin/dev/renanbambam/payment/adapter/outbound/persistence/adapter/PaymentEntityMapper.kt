package dev.renanbambam.payment.adapter.outbound.persistence.adapter

import dev.renanbambam.payment.adapter.outbound.persistence.entity.PaymentEntity
import dev.renanbambam.payment.domain.model.Payment
import org.springframework.stereotype.Component

@Component
class PaymentEntityMapper {

    fun toEntity(payment: Payment): PaymentEntity = PaymentEntity(
        id = payment.id,
        amount = payment.amount,
        currency = payment.currency,
        senderId = payment.senderId,
        receiverId = payment.receiverId,
        status = payment.status,
        createdAt = payment.createdAt,
        updatedAt = payment.updatedAt
    )

    fun toDomain(entity: PaymentEntity): Payment = Payment(
        id = entity.id,
        amount = entity.amount,
        currency = entity.currency,
        senderId = entity.senderId,
        receiverId = entity.receiverId,
        status = entity.status,
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt
    )
}
