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
        constraintViolations.map { "${it.propertyPath} ${it.message}" }
    )

    private fun MethodArgumentNotValidException.toDto(): ErrorDto {
        val fieldErrors = this.bindingResult.fieldErrors.map { "${it.field} ${it.defaultMessage}" }
        val objectErrors = this.bindingResult.globalErrors.map { "${it.objectName} ${it.defaultMessage}" }
        val allErrors = fieldErrors + objectErrors

        return ErrorDto(
            Date(),
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.reasonPhrase,
            "Validation failed. Error count: ${allErrors.size}",
            allErrors
        )
    }
}
