package dev.renanbambam.payment.config

import dev.renanbambam.payment.domain.port.inbound.PublishPaymentPort
import dev.renanbambam.payment.domain.port.inbound.QueryPaymentPort
import dev.renanbambam.payment.domain.port.outbound.PaymentEventPublisher
import dev.renanbambam.payment.domain.port.outbound.PaymentRepository
import dev.renanbambam.payment.domain.service.PaymentPublisherService
import dev.renanbambam.payment.domain.service.PaymentQueryService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

// service é classe pura, sem @Service — monto os beans aqui
@Configuration
class DomainConfig {

    @Bean
    fun publishPaymentPort(
        publisher: PaymentEventPublisher,
        repository: PaymentRepository
    ): PublishPaymentPort = PaymentPublisherService(publisher, repository)

    @Bean
    fun queryPaymentPort(repository: PaymentRepository): QueryPaymentPort =
        PaymentQueryService(repository)
}
