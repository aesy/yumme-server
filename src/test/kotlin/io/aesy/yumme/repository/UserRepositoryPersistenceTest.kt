package io.aesy.yumme.repository

import io.aesy.test.TestType
import io.aesy.test.util.Users
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
        val user = userRepository.save(Users.random())

        expectThat(user.id).isNotNull()
    }

    @Test
    fun `It should be possible to fetch a user by user name`() {
        val userName = "test"
        userRepository.save(User(userName = userName, displayName = "woop", passwordHash = "secret"))

        val result = userRepository.findByUserName(userName)

        expectThat(result).isPresent()
    }

    @Test
    fun `It should be possible to fetch a user by user name and password hash`() {
        val userName = "test"
        val passwordHash = "secret"
        userRepository.save(User(userName = userName, displayName = "woop", passwordHash = passwordHash))

        val result = userRepository.findByUserNameAndPasswordHash(userName, passwordHash)

        expectThat(result).isPresent()
    }
}
