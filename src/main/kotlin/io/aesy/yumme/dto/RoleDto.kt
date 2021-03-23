package io.aesy.yumme.dto

import com.fasterxml.jackson.annotation.JsonProperty

@Dto
data class RoleDto(
    @JsonProperty
    val name: String
)
