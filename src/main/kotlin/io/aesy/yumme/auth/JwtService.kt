package io.aesy.yumme.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.exceptions.JWTVerificationException
import io.aesy.yumme.entity.User
import io.aesy.yumme.service.UserService
import io.aesy.yumme.util.Logging.getLogger
import org.apache.shiro.codec.Base64
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import javax.annotation.PostConstruct

@Service
class JwtService(
    private val userService: UserService
) {
    companion object {
        private val logger = getLogger()
    }

    @Value("\${yumme.security.jwt.signing-key}")
    private lateinit var signingKey: String

    @Value("\${yumme.security.jwt.expiration-minutes}")
    private var expirationMinutes: Long = 0

    @PostConstruct
    private fun init() {
        this.signingKey = Base64.encodeToString(signingKey.toByteArray())
    }

    fun validateToken(token: String, user: User): Boolean {
        val algorithm = Algorithm.HMAC256(signingKey)

        try {
            JWT.require(algorithm)
                .withSubject(user.email)
                .build()
                .verify(token)

            return true
        } catch (e: JWTVerificationException) {
            logger.info("Failed to verify JWT token of user ${user.userName}. ${e.message}")
        }

        return false
    }

    fun createToken(user: User): String {
        val date = Instant.now()
            .plus(expirationMinutes, ChronoUnit.MINUTES)
        val algorithm = Algorithm.HMAC256(signingKey)

        return JWT.create()
            .withSubject(user.email)
            .withExpiresAt(Date.from(date))
            .sign(algorithm)
    }

    fun getUserByToken(token: String): Optional<User> {
        try {
            val userName = JWT.decode(token).subject

            if (userName == null) {
                logger.info("Failed to extract subject from JWT token. It's missing.")

                return Optional.empty()
            }

            return userService.getByUserName(userName)
        } catch (e: JWTDecodeException) {
            logger.info("Failed to decode JWT token to be able to extract subject. ${e.message}")
        }

        return Optional.empty()
    }
}
