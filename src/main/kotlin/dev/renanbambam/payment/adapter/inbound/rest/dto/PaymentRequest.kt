package dev.renanbambam.payment.adapter.inbound.rest.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class PaymentRequest(
    @field:NotNull(message = "amount é obrigatório")
    @field:DecimalMin(value = "0.01", message = "amount deve ser maior que zero")
    val amount: BigDecimal?,

    @field:NotBlank(message = "currency é obrigatório")
    @field:Size(min = 3, max = 3, message = "currency deve ter 3 letras (ISO 4217)")
    val currency: String?,

    @field:NotBlank(message = "senderId é obrigatório")
    val senderId: String?,

    @field:NotBlank(message = "receiverId é obrigatório")
    val receiverId: String?
)
