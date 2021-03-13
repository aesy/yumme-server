package io.aesy.yumme.request

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotEmpty

data class LoginRequest(
    @field:NotEmpty
    @field:JsonProperty("email")
    var email: String,
    @field:NotEmpty
    @field:JsonProperty("password")
    var password: String
)
