package io.aesy.yumme.dto

import com.fasterxml.jackson.annotation.JsonProperty

@Dto
data class CategoryDto(
    @JsonProperty
    val name: String
)
