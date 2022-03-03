package io.aesy.yumme.controller

import io.aesy.test.TestType
import io.aesy.test.util.Users.createUser
import io.aesy.yumme.dto.TokenResponse
import io.aesy.yumme.dto.UserDto
import io.aesy.yumme.service.UserService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.*
import org.springframework.http.*
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import strikt.api.expectThat
import strikt.assertions.*

@TestType.RestApi
class AuthRestApiTest {
    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var userService: UserService

    @Test
    fun `It should be possible generate an access key pair from user credentials`() {
        val params = getPasswordParams()
        val response = restTemplate.postForEntity<TokenResponse>("/auth/token", formParams(params))

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val tokens = response.body!!

        expectThat(tokens.accessToken).isNotBlank()
        expectThat(tokens.refreshToken).isNotBlank()
        expectThat(tokens.expiresIn).isEqualTo(3600)
        expectThat(tokens.tokenType).isEqualTo("bearer")
    }

    @Test
    fun `It should not be possible generate an access key pair with an invalid grant type`() {
        val params = getPasswordParams().apply {
            this["grant_type"] = "invalid"
        }
        val response = restTemplate.postForEntity<Unit>("/auth/token", formParams(params))

        expectThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `It should not be possible generate an access key pair with missing credentials`() {
        val params = getPasswordParams().apply {
            this["username"] = null
            this["password"] = null
        }
        val response = restTemplate.postForEntity<Unit>("/auth/token", formParams(params))

        expectThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `It should not be possible to generate an access key pair with invalid user credentials`() {
        val params = getPasswordParams().apply {
            this["password"] = "invalid"
        }
        val response = restTemplate.postForEntity<Unit>("/auth/token", formParams(params))

        expectThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `It should be possible refresh a token key pair from a refresh token`() {
        val passwordParams = getPasswordParams()
        val tokens = restTemplate.postForObject<TokenResponse>("/auth/token", formParams(passwordParams))
        val refreshParams = getRefreshParams(tokens!!)
        val result = restTemplate.postForObject<TokenResponse>("/auth/token", formParams(refreshParams))!!

        expectThat(result.accessToken).isNotBlank().isNotEqualTo(tokens.accessToken)
        expectThat(result.refreshToken).isEqualTo(tokens.refreshToken)
        expectThat(result.expiresIn).isEqualTo(3600)
        expectThat(result.tokenType).isEqualTo("bearer")
    }

    @Test
    fun `It should be possible to use an access key to access resources`() {
        val params = getPasswordParams()
        val tokens = restTemplate.postForObject<TokenResponse>("/auth/token", formParams(params))!!

        val headers = HttpHeaders()
        headers.add("Authorization", "Bearer ${tokens.accessToken}")

        val userResponse = restTemplate.exchange<UserDto>("/user/me", HttpMethod.GET, HttpEntity<Unit>(headers))

        expectThat(userResponse.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `It should not be possible to use an invalid access key to access resources`() {
        val headers = HttpHeaders()
        headers.add("Authorization", "Bearer invalid")

        val userResponse = restTemplate.exchange<UserDto>("/user/me", HttpMethod.GET, HttpEntity<Unit>(headers))

        expectThat(userResponse.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `It should be possible to revoke a refresh token`() {
        val passwordParams = getPasswordParams()
        val tokens = restTemplate.postForObject<TokenResponse>("/auth/token", formParams(passwordParams))!!
        val revokeParams = LinkedMultiValueMap<String, String>()
        revokeParams["refresh_token"] = tokens.refreshToken
        val response = restTemplate.postForEntity<Unit>("/auth/token/revoke", formParams(revokeParams))

        expectThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
    }

    @Test
    fun `It should not be possible to use an revoked refresh key to generate a new token pair`() {
        val params = getPasswordParams()
        val tokens = restTemplate.postForObject<TokenResponse>("/auth/token", formParams(params))!!
        val revokeParams = LinkedMultiValueMap<String, String>()
        revokeParams["refresh_token"] = tokens.refreshToken
        restTemplate.postForEntity<Unit>("/auth/token/revoke", formParams(revokeParams))

        val refreshParams = getRefreshParams(tokens)
        val response = restTemplate.postForEntity<Unit>("/auth/token", formParams(refreshParams))

        expectThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    private fun getPasswordParams(): MultiValueMap<String, String> {
        val user = userService.createUser("test", "woop", "secret")
        val params = LinkedMultiValueMap<String, String>()
        params["grant_type"] = "password"
        params["username"] = user.userName
        params["password"] = "secret"

        return params
    }

    private fun getRefreshParams(pair: TokenResponse): MultiValueMap<String, String> {
        val params = LinkedMultiValueMap<String, String>()
        params["grant_type"] = "refresh_token"
        params["refresh_token"] = pair.refreshToken

        return params
    }

    private fun formParams(params: MultiValueMap<String, String>): HttpEntity<*> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        return HttpEntity(params, headers)
    }
}
