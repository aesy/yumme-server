package io.aesy.yumme.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.validator.constraints.Length
import javax.validation.constraints.NotEmpty

@Dto
class RegisterRequest(
    @field:Length(min = 4, max = 64)
    @field:NotEmpty
    @field:JsonProperty("user_name")
    var userName: String?,
    @field:Length(min = 4, max = 64)
    @field:NotEmpty
    @field:JsonProperty("display_name")
    var displayName: String?,
    @field:Length(min = 8, max = 256)
    @field:NotEmpty
    @field:JsonProperty("password")
    var password: String?
)
