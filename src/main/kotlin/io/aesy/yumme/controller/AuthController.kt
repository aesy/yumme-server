package io.aesy.yumme.controller

import io.aesy.yumme.auth.TokenProvider
import io.aesy.yumme.dto.*
import io.aesy.yumme.dto.TokenRequest.PasswordGrantType
import io.aesy.yumme.dto.TokenRequest.RefreshTokenGrantType
import io.aesy.yumme.repository.RefreshTokenRepository
import io.aesy.yumme.service.UserService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.*
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional
import javax.validation.Valid
import javax.validation.Validator

@Tag(name = "Token")
@RestController
@RequestMapping(
    "auth",
    consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
@Validated
class AuthController(
    private val userService: UserService,
    private val tokenProvider: TokenProvider,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val validator: Validator
) {
    @PostMapping("/token")
    @Transactional
    fun createAccessToken(
        @Valid @ModelAttribute request: TokenRequest?
    ): ResponseEntity<*> {
        if (request == null) {
            return invalidRequest("Missing request body")
        }

        val errors = when (request.grant_type) {
            "password" -> validator.validate(request, PasswordGrantType::class.java)
            "refresh_token" -> validator.validate(request, RefreshTokenGrantType::class.java)
            else -> return invalidGrant("Unsupported grant type ${request.grant_type}")
        }

        if (errors.isNotEmpty()) {
            return invalidRequest(errors.first().message)
        }

        val user = when (request.grant_type) {
            "password" -> {
                val user = userService.getByUserNameAndPassword(request.username!!, request.password!!)

                if (user.isEmpty) {
                    return invalidGrant("Invalid username or password")
                } else {
                    user.get()
                }
            }
            "refresh_token" -> {
                val token = refreshTokenRepository.findByValue(request.refresh_token!!)

                if (token.isEmpty) {
                    return invalidGrant("Invalid refresh token")
                } else {
                    token.get().user
                }
            }
            else -> return invalidGrant("Unsupported grant type ${request.grant_type}")
        }

        val tokenPair = tokenProvider.getOrCreateTokenPair(user)
        val response = TokenResponse(
            accessToken = tokenPair.accessToken.value,
            refreshToken = tokenPair.refreshToken.value,
            expiresIn = tokenPair.accessToken.expiresInSeconds
        )

        return ResponseEntity.ok(response)
    }

    @PostMapping("/token/revoke")
    @Transactional
    fun revokeRefreshToken(
        @Valid @ModelAttribute request: RevokeRefreshTokenRequest?
    ): ResponseEntity<*> {
        if (request == null) {
            return invalidRequest("Missing request body")
        } else if (request.refresh_token == null) {
            return invalidRequest("Missing refresh token")
        }

        val token = refreshTokenRepository.findByValue(request.refresh_token!!)

        val user = if (token.isEmpty) {
            return invalidRequest("Invalid refresh token")
        } else {
            token.get().user
        }

        tokenProvider.revokeAccess(user)

        return ResponseEntity.noContent().build<Unit>()
    }

    private fun invalidRequest(description: String): ResponseEntity<AuthErrorDto> {
        val error = AuthErrorDto("invalid_request", description)

        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    private fun invalidGrant(description: String): ResponseEntity<AuthErrorDto> {
        val error = AuthErrorDto("unsupported_grant_type", description)

        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }
}
