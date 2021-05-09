package io.aesy.yumme.dto

import com.fasterxml.jackson.annotation.JsonProperty

@Dto
class CategoryDto(
    @JsonProperty
    var name: String?
)
