package io.aesy.yumme.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.aesy.yumme.util.Strings

@Dto
class IngredientDto(
    @JsonProperty
    var name: String? = Strings.random(10),
)
