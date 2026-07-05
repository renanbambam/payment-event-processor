package dev.renanbambam.payment.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

// os pacotes de entity e repository são separados na arquitetura hexagonal,
// então aponto explicitamente onde o Spring Data deve procurar
@Configuration
@EntityScan(basePackages = ["dev.renanbambam.payment.adapter.outbound.persistence.entity"])
@EnableJpaRepositories(basePackages = ["dev.renanbambam.payment.adapter.outbound.persistence.repository"])
class DatabaseConfig
