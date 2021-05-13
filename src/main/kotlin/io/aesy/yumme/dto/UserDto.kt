package io.aesy.yumme.dto

import com.fasterxml.jackson.annotation.JsonProperty

@Dto
class UserDto(
    @JsonProperty("id")
    var id: Long?,

    @JsonProperty("user_name")
    var userName: String?,

    @JsonProperty("display_name")
    var displayName: String?,
)
