package io.aesy.yumme.request

import com.fasterxml.jackson.annotation.JsonProperty
import io.aesy.yumme.dto.Dto
import org.hibernate.validator.constraints.Length
import javax.validation.constraints.NotEmpty

@Dto
data class LoginRequest(
    @field:NotEmpty
    @field:Length(min = 4, max = 128)
    @field:JsonProperty("email")
    var email: String?,
    @field:NotEmpty
    @field:Length(min = 4, max = 64)
    @field:JsonProperty("password")
    var password: String?
)
