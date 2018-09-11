package io.aesy.food.controller

import io.aesy.food.auth.JwtService
import io.aesy.food.exception.InvalidCredentials
import io.aesy.food.request.LoginRequest
import io.aesy.food.service.UserService
import org.slf4j.LoggerFactory
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
        private val logger = LoggerFactory.getLogger(AuthController::class.java)
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
