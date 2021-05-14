package io.aesy.yumme.dto

import com.fasterxml.jackson.annotation.JsonProperty

@Dto
class IngredientDto(
    @JsonProperty
    var name: String
)
