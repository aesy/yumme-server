package io.aesy.yumme.controller

import io.aesy.yumme.auth.JwtService
import io.aesy.yumme.exception.InvalidCredentials
import io.aesy.yumme.request.LoginRequest
import io.aesy.yumme.service.UserService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional
import javax.validation.Valid

@Tag(name = "Token")
@RestController
@RequestMapping("auth")
class TokenController(
    private val userService: UserService,
    private val authService: JwtService
) {
    @PostMapping("/access_token")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    fun createAccessToken(
        @Valid @RequestBody request: LoginRequest
    ): String {
        val email = request.email!!
        val password = request.password!!
        val user = userService.getByEmailAndPassword(email, password)
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
