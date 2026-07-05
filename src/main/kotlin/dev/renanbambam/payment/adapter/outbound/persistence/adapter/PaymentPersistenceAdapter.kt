package dev.renanbambam.payment.adapter.outbound.persistence.adapter

import dev.renanbambam.payment.adapter.outbound.persistence.repository.PaymentJpaRepository
import dev.renanbambam.payment.domain.model.Payment
import dev.renanbambam.payment.domain.port.outbound.PaymentRepository
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class PaymentPersistenceAdapter(
    private val jpaRepository: PaymentJpaRepository,
    private val mapper: PaymentEntityMapper
) : PaymentRepository {

    override fun save(payment: Payment): Payment {
        val entity = mapper.toEntity(payment)
        return mapper.toDomain(jpaRepository.save(entity))
    }

    override fun findById(id: String): Payment? =
        jpaRepository.findById(id).getOrNull()?.let { mapper.toDomain(it) }
}
