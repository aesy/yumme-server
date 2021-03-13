package io.aesy.yumme.response

import com.fasterxml.jackson.annotation.JsonProperty

class RegisterResponse(
    @field:JsonProperty("access_token")
    var accessToken: String,
    @field:JsonProperty("refresh_token")
    var refreshToken: String
)
