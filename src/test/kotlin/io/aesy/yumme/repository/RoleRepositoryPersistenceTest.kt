package io.aesy.yumme.repository

import io.aesy.test.TestType
import io.aesy.yumme.entity.Role
import io.aesy.yumme.entity.User
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import strikt.api.expectThat
import strikt.assertions.*

@TestType.Persistence
class RoleRepositoryPersistenceTest {
    @Autowired
    private lateinit var roleRepository: RoleRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `It should be possible to persist a role`() {
        val role = Role(name = "test")
        roleRepository.save(role)

        expectThat(role.id).isNotNull()
    }

    @Test
    fun `It should be possible to fetch roles by user`() {
        val role = Role(name = "test")
        roleRepository.save(role)

        val email = "test@test.com"
        val user = User(email = email, password = "secret")
        user.roles += role
        userRepository.save(user)

        val result = roleRepository.findAllByUser(user)

        expectThat(result).map(Role::id).containsExactly(role.id)
    }
}
