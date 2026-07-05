package dev.renanbambam.payment.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun paymentApi(): OpenAPI = OpenAPI().info(
        Info()
            .title("payment-event-processor")
            .description("Processamento de eventos de pagamento orientado a eventos")
            .version("0.1.0")
    )
}
