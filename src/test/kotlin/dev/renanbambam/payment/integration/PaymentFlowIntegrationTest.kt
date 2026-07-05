package dev.renanbambam.payment.integration

import dev.renanbambam.payment.adapter.inbound.rest.dto.PaymentResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.time.Duration
import java.time.Instant

// sobe Kafka e Postgres de verdade: valida o fluxo REST -> producer -> consumer -> banco
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class PaymentFlowIntegrationTest {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun `cria pagamento e consumer marca como processing`() {
        val request = mapOf(
            "amount" to "150.00",
            "currency" to "BRL",
            "senderId" to "user-1",
            "receiverId" to "user-2"
        )

        val created = restTemplate.postForEntity("/api/v1/payments", request, PaymentResponse::class.java)
        assertEquals(HttpStatus.CREATED, created.statusCode)
        val id = requireNotNull(created.body).id

        // o consumer é assíncrono: espera o status virar PROCESSING
        val status = awaitStatus(id, PROCESSING)
        assertEquals(PROCESSING, status)
    }

    private fun awaitStatus(id: String, expected: String): String {
        val deadline = Instant.now().plus(Duration.ofSeconds(30))
        var last = ""
        while (Instant.now().isBefore(deadline)) {
            val response = restTemplate.getForEntity("/api/v1/payments/$id", PaymentResponse::class.java)
            if (response.statusCode == HttpStatus.OK) {
                last = response.body?.status ?: ""
                if (last == expected) return last
            }
            Thread.sleep(500)
        }
        return last
    }

    companion object {
        private const val PROCESSING = "PROCESSING"

        @Container
        @JvmStatic
        val postgres = PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName("payments")
            .withUsername("payments")
            .withPassword("payments")

        @Container
        @JvmStatic
        val kafka = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"))

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers)
        }
    }
}
