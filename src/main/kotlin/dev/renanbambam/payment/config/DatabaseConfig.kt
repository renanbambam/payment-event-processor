package dev.renanbambam.payment.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

// entity e repository ficam em pacotes separados, então aponto os dois na mão
@Configuration
@EntityScan(basePackages = ["dev.renanbambam.payment.adapter.outbound.persistence.entity"])
@EnableJpaRepositories(basePackages = ["dev.renanbambam.payment.adapter.outbound.persistence.repository"])
class DatabaseConfig
