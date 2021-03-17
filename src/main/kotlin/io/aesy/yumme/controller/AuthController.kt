package io.aesy.yumme.controller

import io.aesy.yumme.auth.JwtService
import io.aesy.yumme.exception.InvalidCredentials
import io.aesy.yumme.request.LoginRequest
import io.aesy.yumme.service.UserService
import io.aesy.yumme.util.getLogger
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional
import javax.validation.Valid

@RestController
@RequestMapping("auth")
class AuthController(
    private val userService: UserService,
    private val authService: JwtService
) {
    companion object {
        private val logger = getLogger()
    }

    @PostMapping("/access_token")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    fun getAccessToken(
        @Valid @RequestBody request: LoginRequest
    ): String {
        val user = userService.getByEmailAndPassword(request.email, request.password)
            .orElseThrow { InvalidCredentials() }

        return authService.createToken(user)
    }

    @PostMapping("/refresh_token")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    fun refreshToken() {
        throw NotImplementedError()
    }
}
