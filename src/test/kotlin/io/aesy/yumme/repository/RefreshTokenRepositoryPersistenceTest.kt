package io.aesy.yumme.repository

import io.aesy.test.TestType
import io.aesy.test.util.Users
import io.aesy.yumme.entity.RefreshToken
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import strikt.java.isPresent

@TestType.Persistence
class RefreshTokenRepositoryPersistenceTest {
    @Autowired
    private lateinit var refreshTokenRepository: RefreshTokenRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `It should be possible to persist a refresh token`() {
        val user = userRepository.save(Users.random())
        val refreshToken = refreshTokenRepository.save(RefreshToken(value = "", user = user))

        expectThat(refreshToken.id).isNotNull()
    }

    @Test
    fun `It should be possible to fetch a refresh token by user`() {
        val user1 = userRepository.save(Users.random())
        val user2 = userRepository.save(Users.random())
        val refreshToken = refreshTokenRepository.save(RefreshToken(value = "def", user = user2))
        refreshTokenRepository.save(RefreshToken(value = "abc", user = user1))

        val result = refreshTokenRepository.findFirstByUser(user2)

        expectThat(result)
            .isPresent()
            .get { expectThat(id).isEqualTo(refreshToken.id) }
    }

    @Test
    fun `It should be possible to fetch a refresh token by value`() {
        val user = userRepository.save(Users.random())
        refreshTokenRepository.save(RefreshToken(value = "abc", user = user))

        val result = refreshTokenRepository.findByValue("abc")

        expectThat(result).isPresent()
    }
}
