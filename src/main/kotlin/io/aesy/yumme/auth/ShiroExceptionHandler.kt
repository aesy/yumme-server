package io.aesy.yumme.auth

import org.apache.shiro.authz.AuthorizationException
import org.apache.shiro.authz.UnauthenticatedException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

// TODO use ErrorDTO
@RestControllerAdvice
class ShiroExceptionHandler {
    @ExceptionHandler(UnauthenticatedException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleException(exception: UnauthenticatedException) {}

    @ExceptionHandler(AuthorizationException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleException(exception: AuthorizationException) {}
}
