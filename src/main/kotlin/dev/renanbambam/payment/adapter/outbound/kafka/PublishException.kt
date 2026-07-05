package dev.renanbambam.payment.adapter.outbound.kafka

class PublishException(message: String, cause: Throwable) : RuntimeException(message, cause)
