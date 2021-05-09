package io.aesy.yumme.exception

import io.aesy.yumme.dto.ErrorDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestControllerAdvice
class ResponseStatusExceptionHandler {
    @ExceptionHandler(ResponseStatusException::class)
    fun generateNotFoundException(exception: ResponseStatusException): ResponseEntity<ErrorDto> = ResponseEntity
        .status(exception.status)
        .body(exception.toDto())

    private fun ResponseStatusException.toDto(): ErrorDto = ErrorDto(
        Date(),
        status.value(),
        status.reasonPhrase,
        message
    )
}
