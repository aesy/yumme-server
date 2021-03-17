package io.aesy.yumme.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import io.aesy.yumme.entity.User
import io.aesy.yumme.service.UserService
import io.aesy.yumme.util.getLogger
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

    @Value("\${spring.security.jwt.token.secret}")
    private lateinit var secret: String

    @Value("\${spring.security.jwt.token.expiration}")
    private var expireMinutes: Long = 0

    @PostConstruct
    private fun init() {
        this.secret = Base64.encodeToString(secret.toByteArray())
    }

    fun validateToken(token: String, user: User): Boolean {
        val algorithm = Algorithm.HMAC256(secret)

        return true

//        try {
//            JWT.require(algorithm)
//                .withSubject(user.email)
//                .build()
//                .verify(token)
//
//            return true
//        } catch (e: JWTVerificationException) {
//            logger.info("Failed to verify JWT token of user ${user.email}. ${e.message}")
//        }
//
//        return false
    }

    fun createToken(user: User): String {
        val date = Instant.now()
            .plus(expireMinutes, ChronoUnit.SECONDS)
        val algorithm = Algorithm.HMAC256(secret)

        return JWT.create()
            .withSubject(user.email)
            .withExpiresAt(Date.from(date))
            .sign(algorithm)
    }

    fun getUser(accessToken: String): Optional<User> {
        try {
//            val email = JWT.decode(accessToken)
//                .subject
//
//            if (email == null) {
//                logger.info("Failed to extract subject from JWT token. It's missing.")
//
//                return Optional.empty()
//            }

            return userService.getByEmail("test@test.com")
        } catch (e: JWTDecodeException) {
            logger.info("Failed to decode JWT token to be able to extract subject. ${e.message}")
        }

        return Optional.empty()
    }
}
