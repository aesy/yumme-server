package io.aesy.yumme.controller

import io.aesy.test.TestType
import io.aesy.yumme.dto.ConfigDto
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull

@TestType.RestApi
class ConfigRestApiTest {
    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun `It should be possible to fetch the application config`() {
        val response = restTemplate.getForEntity<ConfigDto>("/config")

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isNotNull()
    }
}
