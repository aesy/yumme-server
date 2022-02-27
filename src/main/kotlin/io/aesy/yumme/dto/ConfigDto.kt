package io.aesy.yumme.dto

import com.fasterxml.jackson.annotation.JsonProperty

@Dto
class ConfigDto(
    @JsonProperty("registration_enabled")
    var registrationEnabled: Boolean
)
