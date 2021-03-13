package io.aesy.yumme.dto

import com.fasterxml.jackson.annotation.JsonProperty

@Dto
data class TagDto(
    @JsonProperty
    val name: String
)
