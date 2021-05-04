package io.aesy.yumme.auth

import io.aesy.yumme.entity.RefreshToken
import io.aesy.yumme.entity.User
import io.aesy.yumme.repository.RefreshTokenRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.time.Instant
import java.util.*

@Service
class TokenProvider(
    private val jwtService: JwtService,
    private val refreshTokenRepository: RefreshTokenRepository
) {
    companion object {
        private val random = SecureRandom()
    }

    @Value("\${yumme.security.jwt.expiration-minutes}")
    private var expireMinutes: Long = 0

    fun getOrCreateTokenPair(user: User): TokenPair {
        val accessToken = createAccessToken(user)
        val refreshToken = getOrCreateRefreshToken(user)

        return TokenPair(accessToken, refreshToken)
    }

    fun revokeAccess(user: User) {
        refreshTokenRepository.findFirstByUser(user)
            .map { it.apply { revokedAt = Instant.now() } }
            .ifPresent(refreshTokenRepository::save)
    }

    private fun createAccessToken(user: User): AccessToken {
        val value = jwtService.createToken(user)

        return AccessToken(value = value, expiresInSeconds = expireMinutes * 60)
    }

    private fun getOrCreateRefreshToken(user: User): RefreshToken {
        return refreshTokenRepository.findFirstByUser(user)
            .map { it.apply { lastUsedAt = Instant.now() } }
            .map { it.apply { revokedAt = null } }
            .orElseGet { createRefreshToken(user) }
    }

    private fun createRefreshToken(user: User): RefreshToken {
        val bytes = ByteArray(32).apply { random.nextBytes(this) }
        val value = Base64.getEncoder().encodeToString(bytes)
        val refreshToken = RefreshToken(value = value, user = user)

        return refreshTokenRepository.save(refreshToken)
    }
}
