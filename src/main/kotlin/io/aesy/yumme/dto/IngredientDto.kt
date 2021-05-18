package io.aesy.yumme.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.validator.constraints.Length

@Dto
class IngredientDto(
    @JsonProperty
    @Length(min = 1, max = 64)
    var name: String
)
