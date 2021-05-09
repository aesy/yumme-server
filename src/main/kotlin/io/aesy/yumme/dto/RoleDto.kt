package io.aesy.yumme.dto

import com.fasterxml.jackson.annotation.JsonProperty

@Dto
class RoleDto(
    @JsonProperty
    var name: String?
)
