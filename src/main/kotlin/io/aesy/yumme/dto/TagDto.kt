package io.aesy.yumme.dto

import com.fasterxml.jackson.annotation.JsonProperty

@Dto
class TagDto(
    @JsonProperty
    var name: String?
)
