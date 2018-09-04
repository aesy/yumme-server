package io.aesy.food.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class UserDto(
    @JsonProperty
    var email: String?
) {
    constructor(): this(null)
}
