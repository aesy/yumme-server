package io.aesy.yumme.dto

import com.fasterxml.jackson.annotation.JsonProperty

@Dto
class TokenResponse(
    @field:JsonProperty("access_token")
    var accessToken: String,
    @field:JsonProperty("refresh_token")
    var refreshToken: String,
    @field:JsonProperty("expires_in")
    val expiresIn: Long
) {
    @field:JsonProperty("token_type")
    val tokenType: String = "bearer"
}
