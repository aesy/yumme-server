package io.aesy.yumme.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

@Dto
data class RecipeDto(
    @JsonProperty
    var id: Long?,

    @JsonProperty
    var title: String?,

    @JsonProperty
    var description: String?,

    @JsonProperty
    var rating: RatingDto?
) {
    @JsonProperty
    var image = "https://loremflickr.com/320/420?hash=${UUID.randomUUID()}"
}
