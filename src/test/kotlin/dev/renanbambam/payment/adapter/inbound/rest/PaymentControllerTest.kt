package dev.renanbambam.payment.adapter.inbound.rest

import com.fasterxml.jackson.databind.ObjectMapper
import dev.renanbambam.payment.adapter.inbound.rest.mapper.PaymentRestMapper
import dev.renanbambam.payment.domain.model.Payment
import dev.renanbambam.payment.domain.model.PaymentEvent
import dev.renanbambam.payment.domain.model.PaymentEventType
import dev.renanbambam.payment.domain.model.PaymentStatus
import dev.renanbambam.payment.domain.port.inbound.PublishPaymentPort
import dev.renanbambam.payment.domain.port.inbound.QueryPaymentPort
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.time.Instant

@WebMvcTest(PaymentController::class)
@Import(PaymentRestMapper::class)
class PaymentControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var publishPort: PublishPaymentPort

    @MockBean
    private lateinit var queryPort: QueryPaymentPort

    @Test
    fun `cria pagamento e retorna 201`() {
        whenever(publishPort.publish(any())).thenAnswer {
            val payment = it.getArgument<Payment>(0)
            PaymentEvent("evt-1", payment.id, PaymentEventType.PAYMENT_CREATED, payment, Instant.now())
        }

        val body = mapOf(
            "amount" to "150.00",
            "currency" to "BRL",
            "senderId" to "user-1",
            "receiverId" to "user-2"
        )

        mockMvc.perform(
            post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andExpect(jsonPath("$.currency").value("BRL"))
    }

    @Test
    fun `rejeita pagamento sem valor com 400`() {
        val body = mapOf(
            "currency" to "BRL",
            "senderId" to "user-1",
            "receiverId" to "user-2"
        )

        mockMvc.perform(
            post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `retorna 404 quando pagamento nao existe`() {
        whenever(queryPort.findById("nope")).thenReturn(null)

        mockMvc.perform(get("/api/v1/payments/nope"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `retorna pagamento existente`() {
        val now = Instant.now()
        val payment = Payment(
            id = "pay-1",
            amount = BigDecimal("150.00"),
            currency = "BRL",
            senderId = "user-1",
            receiverId = "user-2",
            status = PaymentStatus.PROCESSING,
            createdAt = now,
            updatedAt = now
        )
        whenever(queryPort.findById("pay-1")).thenReturn(payment)

        mockMvc.perform(get("/api/v1/payments/pay-1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("pay-1"))
            .andExpect(jsonPath("$.status").value("PROCESSING"))
    }
}
