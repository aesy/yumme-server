package io.aesy.yumme.request

import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.validator.constraints.Length
import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty

data class RegisterRequest(
    @field:Length(min = 1, max = 64)
    @field:NotEmpty
    @field:JsonProperty("first_name")
    var firstName: String?,
    @field:Length(min = 1, max = 64)
    @field:NotEmpty
    @field:JsonProperty("last_name")
    var lastName: String?,
    @field:Length(min = 4, max = 128)
    @field:Email
    @field:NotEmpty
    @field:JsonProperty("email")
    var email: String?,
    @field:Length(min = 4, max = 64)
    @field:NotEmpty
    @field:JsonProperty("password")
    var password: String?
)
