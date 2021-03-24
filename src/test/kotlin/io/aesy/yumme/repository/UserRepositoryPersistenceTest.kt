package io.aesy.yumme.repository

import io.aesy.test.TestType
import io.aesy.yumme.entity.User
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import strikt.api.expectThat
import strikt.assertions.isNotNull
import strikt.java.isPresent

@TestType.Persistence
class UserRepositoryPersistenceTest {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `It should be possible to persist a user`() {
        val user = User(email = "test@test.com", password = "secret")
        userRepository.save(user)

        expectThat(user.id).isNotNull()
    }

    @Test
    fun `It should be possible to fetch a user by email`() {
        val email = "test@test.com"
        val user = User(email = email, password = "secret")
        userRepository.save(user)

        val result = userRepository.findByEmail(email)

        expectThat(result).isPresent()
    }

    @Test
    fun `It should be possible to fetch a user by email and password`() {
        val email = "test@test.com"
        val password = "secret"
        val user = User(email = email, password = password)
        userRepository.save(user)

        val result = userRepository.findByEmailAndPassword(email, password)

        expectThat(result).isPresent()
    }
}
