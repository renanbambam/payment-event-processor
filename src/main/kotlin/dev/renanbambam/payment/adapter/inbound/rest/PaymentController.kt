package dev.renanbambam.payment.adapter.inbound.rest

import dev.renanbambam.payment.adapter.inbound.rest.dto.PaymentRequest
import dev.renanbambam.payment.adapter.inbound.rest.dto.PaymentResponse
import dev.renanbambam.payment.adapter.inbound.rest.mapper.PaymentRestMapper
import dev.renanbambam.payment.domain.model.PaymentNotFoundException
import dev.renanbambam.payment.domain.port.inbound.PublishPaymentPort
import dev.renanbambam.payment.domain.port.inbound.QueryPaymentPort
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/payments")
class PaymentController(
    private val publishPort: PublishPaymentPort,
    private val queryPort: QueryPaymentPort,
    private val mapper: PaymentRestMapper
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: PaymentRequest): PaymentResponse {
        val payment = mapper.toDomain(request)
        val event = publishPort.publish(payment)
        return mapper.toResponse(event)
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): PaymentResponse {
        val payment = queryPort.findById(id) ?: throw PaymentNotFoundException(id)
        return mapper.toResponse(payment)
    }
}
