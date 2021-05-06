package io.aesy.yumme.controller

import io.aesy.test.TestType
import io.aesy.yumme.dto.RegisterRequest
import io.aesy.yumme.dto.UserDto
import io.aesy.yumme.service.UserService
import io.aesy.yumme.util.Users.createUser
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.*
import org.springframework.http.HttpStatus
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull

@TestType.RestApi
class UserRestApiTest {
    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var userService: UserService

    @Test
    fun `It should be possible to fetch the authenticated user`() {
        val user = userService.createUser("test", "woop", "secret123")
        val response = restTemplate.withBasicAuth(user.userName, "secret123")
            .getForEntity<UserDto>("/user/me")

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isNotNull()
    }

    @Test
    fun `It should not be possible to fetch an unauthenticated user`() {
        val response = restTemplate.getForEntity<Unit>("/user/me")

        expectThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `It should be possible to register a new user`() {
        val request = RegisterRequest("test", "woop", "secret123")
        val response = restTemplate.postForEntity<String>("/user/register", request)

        expectThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
    }

    @Test
    fun `It should not be possible to register a user with an already used name`() {
        val user = userService.createUser("test", "woop", "secret123")
        val request = RegisterRequest(user.userName, "woop", "secret321")
        val response = restTemplate.postForEntity<Unit>("/user/register", request)

        expectThat(response.statusCode).isEqualTo(HttpStatus.CONFLICT)
    }
}
