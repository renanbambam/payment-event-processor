package dev.renanbambam.payment.adapter.outbound.persistence.repository

import dev.renanbambam.payment.adapter.outbound.persistence.entity.PaymentEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentJpaRepository : JpaRepository<PaymentEntity, String>
