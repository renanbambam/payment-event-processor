package dev.renanbambam.payment.domain.model

class PaymentNotFoundException(id: String) :
    RuntimeException("pagamento não encontrado: $id")
