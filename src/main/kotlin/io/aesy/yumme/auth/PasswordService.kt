package io.aesy.yumme.auth

import at.favre.lib.crypto.bcrypt.BCrypt
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class PasswordService {
    @Value("\${spring.security.pepper:secret}")
    private lateinit var pepper: String

    fun encodePassword(password: String): String {
        return BCrypt.withDefaults().hashToString(12, (pepper + password).toCharArray())
    }

    fun verifyPassword(password: String, hash: String): Boolean {
        val result = BCrypt.verifyer().verify((pepper + password).toCharArray(), hash)

        return result.verified
    }
}
