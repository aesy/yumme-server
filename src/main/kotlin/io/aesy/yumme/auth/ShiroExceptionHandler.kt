package io.aesy.yumme.auth

import io.aesy.yumme.dto.ErrorDto
import org.apache.shiro.authz.AuthorizationException
import org.apache.shiro.authz.UnauthenticatedException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestControllerAdvice
class ShiroExceptionHandler {
    @ExceptionHandler(UnauthenticatedException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleException(exception: UnauthenticatedException): ErrorDto = exception.toDto()

    @ExceptionHandler(AuthorizationException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleException(exception: AuthorizationException): ErrorDto = exception.toDto()

    private fun UnauthenticatedException.toDto(): ErrorDto = ErrorDto(
        Date(),
        HttpStatus.UNAUTHORIZED.value(),
        HttpStatus.UNAUTHORIZED.reasonPhrase,
        "Missing or invalid credentials"
    )

    private fun AuthorizationException.toDto(): ErrorDto = ErrorDto(
        Date(),
        HttpStatus.FORBIDDEN.value(),
        HttpStatus.FORBIDDEN.reasonPhrase,
        "Insufficient permissions"
    )
}
