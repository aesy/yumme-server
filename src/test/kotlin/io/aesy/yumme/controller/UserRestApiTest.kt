package io.aesy.yumme.controller

import io.aesy.test.TestType
import io.aesy.yumme.dto.UserDto
import io.aesy.yumme.entity.User
import io.aesy.yumme.request.RegisterRequest
import io.aesy.yumme.service.UserService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.*
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import strikt.api.expectThat
import strikt.assertions.*

@TestType.RestApi
class UserRestApiTest {
    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var userService: UserService

    @Test
    fun `It should be possible to fetch the authenticated user`() {
        val email = "test@test.com"
        val password = "secret"
        userService.save(User(email = email, password = password))
        val response = restTemplate.withBasicAuth(email, password)
            .getForEntity<UserDto>("/user/me")

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isNotNull()
    }

    @Test
    fun `It should not be possible to fetch an unauthenticated user`() {
        val response = restTemplate.getForEntity<UserDto>("/user/me")

        expectThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `It should be possible to register a new user`() {
        val request = RegisterRequest("test", "test", "test@test.com", "secret")
        val response = restTemplate.postForEntity<String>("/user/register", request)

        expectThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        expectThat(response.body).isNotNull().isNotBlank()
        expectThat(response.headers).hasEntry(HttpHeaders.CONTENT_TYPE, listOf("text/plain;charset=UTF-8"))
    }

    @Test
    fun `It should not be possible to register a user with an already used email`() {
        val email = "conflict@test.com"
        userService.save(User(email = email, password = "secret"))
        val request = RegisterRequest("test", "test", email, "secret")
        val response = restTemplate.postForEntity<String>("/user/register", request)

        expectThat(response.statusCode).isEqualTo(HttpStatus.CONFLICT)
    }
}
