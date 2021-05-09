package io.aesy.yumme.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.*
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

@Dto
@JsonInclude(Include.NON_NULL)
class ErrorDto(
    @field:JsonProperty
    val timestamp: Date,

    @field:JsonProperty
    val status: Int,

    @field:JsonProperty
    val error: String,

    @field:JsonProperty
    val message: String,

    @field:JsonProperty
    val errors: List<String>? = null
)
