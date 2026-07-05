package dev.renanbambam.payment.adapter.inbound.rest

import dev.renanbambam.payment.domain.model.PaymentNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class RestExceptionHandler {

    @ExceptionHandler(PaymentNotFoundException::class)
    fun handleNotFound(ex: PaymentNotFoundException): ResponseEntity<ApiError> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiError(HttpStatus.NOT_FOUND.value(), ex.message ?: "pagamento não encontrado"))

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ApiError> {
        // junta os campos inválidos num único corpo em vez de devolver o stack do Spring
        val fields = ex.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "inválido") }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiError(HttpStatus.BAD_REQUEST.value(), "requisição inválida", fields))
    }
}

data class ApiError(
    val status: Int,
    val message: String,
    val fields: Map<String, String> = emptyMap()
)
