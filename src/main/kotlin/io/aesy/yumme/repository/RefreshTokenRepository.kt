package io.aesy.yumme.repository

import io.aesy.yumme.entity.RefreshToken
import io.aesy.yumme.entity.User
import org.springframework.data.repository.CrudRepository
import java.util.*

interface RefreshTokenRepository: CrudRepository<RefreshToken, Long> {
    fun findFirstByUser(user: User): Optional<RefreshToken>
    fun findByValue(value: String): Optional<RefreshToken>
}
