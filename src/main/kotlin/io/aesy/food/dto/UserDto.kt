package io.aesy.food.dto

import com.fasterxml.jackson.annotation.JsonProperty

@Dto
data class UserDto(
    @JsonProperty
    var email: String?
)
