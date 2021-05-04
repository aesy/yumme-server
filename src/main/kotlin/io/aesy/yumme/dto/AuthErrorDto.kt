package io.aesy.yumme.dto

import com.fasterxml.jackson.annotation.JsonProperty

@Dto
class AuthErrorDto(
    @field:JsonProperty("error")
    val error: String,

    @field:JsonProperty("error_description")
    val description: String
)
