package io.aesy.yumme.dto

import com.fasterxml.jackson.annotation.JsonProperty

@Dto
class UserDto(
    @JsonProperty
    var name: String?
)
