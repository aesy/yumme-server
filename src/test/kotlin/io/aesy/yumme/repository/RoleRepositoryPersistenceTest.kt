package io.aesy.yumme.repository

import io.aesy.test.TestType
import io.aesy.test.util.Users
import io.aesy.yumme.entity.Role
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
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
    fun `It should be possible to fetch all roles in pages`() {
        val role = Role(name = "test")
        roleRepository.save(role)

        val result = roleRepository.findAll(PageRequest.of(0, 2))

        expectThat(result)
            .map(Role::name)
            .containsExactlyInAnyOrder(Role.USER, Role.ADMIN)
    }

    @Test
    fun `It should be possible to fetch roles by name`() {
        val role = Role(name = "test")
        roleRepository.save(role)

        val result = roleRepository.findByName("test")

        expectThat(result.get().name).isEqualTo("test")
    }

    @Test
    fun `It should be possible to fetch roles by user`() {
        val role = Role(name = "test")
        roleRepository.save(role)

        val user = Users.random()
        user.roles.plusAssign(role)
        userRepository.save(user)

        val result = roleRepository.findAllByUser(user)

        expectThat(result)
            .map(Role::id)
            .containsExactly(role.id)
    }
}
