package io.aesy.yumme.controller

import io.aesy.test.TestType
import io.aesy.test.util.HTTP.getList
import io.aesy.test.util.Users.createAdmin
import io.aesy.test.util.Users.createUser
import io.aesy.yumme.dto.ErrorDto
import io.aesy.yumme.dto.RoleDto
import io.aesy.yumme.entity.Role
import io.aesy.yumme.service.UserService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus
import strikt.api.expectThat
import strikt.assertions.*

@TestType.RestApi
class RoleRestApiTest {
    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var userService: UserService

    @Test
    fun `It should be possible to fetch all roles as an admin`() {
        val user = userService.createAdmin("test", "woop", "secret")

        val response = restTemplate.withBasicAuth(user.userName, "secret")
            .getList<RoleDto>("/role")

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val roles = response.body!!

        expectThat(roles)
            .map(RoleDto::name)
            .containsExactlyInAnyOrder(Role.USER, Role.ADMIN)
    }

    @Test
    fun `It should not be possible to fetch all roles as a user`() {
        val user = userService.createUser("test", "woop", "secret")

        val response = restTemplate.withBasicAuth(user.userName, "secret")
            .getForEntity<ErrorDto>("/role")

        expectThat(response.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
    }

    @Test
    fun `It should be possible to fetch roles by user as an admin`() {
        val user = userService.createAdmin("test", "woop", "secret")

        val response = restTemplate.withBasicAuth(user.userName, "secret")
            .getList<RoleDto>("/user/${user.id}/role")

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val roles = response.body!!

        expectThat(roles)
            .map(RoleDto::name)
            .containsExactly(Role.ADMIN)
    }

    @Test
    fun `It should not be possible to fetch roles by user as a user`() {
        val user = userService.createUser("test", "woop", "secret")

        val response = restTemplate.withBasicAuth(user.userName, "secret")
            .getForEntity<ErrorDto>("/user/${user.id}/role")

        expectThat(response.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
    }

    @Test
    fun `It should be possible to fetch roles of the authenticated user`() {
        val user = userService.createUser("test", "woop", "secret")

        val response = restTemplate.withBasicAuth(user.userName, "secret")
            .getList<RoleDto>("/user/me/role")

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val roles = response.body!!

        expectThat(roles)
            .map(RoleDto::name)
            .containsExactly(Role.USER)
    }
}
