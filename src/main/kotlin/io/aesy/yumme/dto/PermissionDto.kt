package io.aesy.yumme.dto

import com.fasterxml.jackson.annotation.JsonProperty

@Dto
data class PermissionDto(
    @JsonProperty
    val name: String
)
