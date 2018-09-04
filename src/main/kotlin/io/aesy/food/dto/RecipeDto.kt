package io.aesy.food.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class RecipeDto(
    @JsonProperty
    var title: String?,

    @JsonProperty
    var description: String?,

    @JsonProperty
    var rating: RatingDto?
) {
    constructor(): this(null, null, null)

    @JsonProperty
    var image = "https://loremflickr.com/320/420?hash=${UUID.randomUUID()}"
}
