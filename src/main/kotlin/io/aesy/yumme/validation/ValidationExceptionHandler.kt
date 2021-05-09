package io.aesy.yumme.validation

import io.aesy.yumme.dto.ErrorDto
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.ConstraintViolationException

@RestControllerAdvice
class ValidationExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException::class)
    fun handle(exception: ConstraintViolationException): ErrorDto = exception.toDto()

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handle(exception: MethodArgumentNotValidException): ErrorDto = exception.toDto()

    private fun ConstraintViolationException.toDto(): ErrorDto = ErrorDto(
        Date(),
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.reasonPhrase,
        "Validation failed. Error count: ${constraintViolations.size}",
        constraintViolations.map { "${it.propertyPath} ${it.message}" }.toList()
    )

    private fun MethodArgumentNotValidException.toDto(): ErrorDto = ErrorDto(
        Date(),
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.reasonPhrase,
        "Validation failed. Error count: ${this.bindingResult.errorCount}",
        this.bindingResult.allErrors.map { "${it.objectName} ${it.defaultMessage}" }.toList()
    )
}
