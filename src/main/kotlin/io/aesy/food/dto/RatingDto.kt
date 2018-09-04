package io.aesy.food.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class RatingDto(
    @JsonProperty
    var average: Double?,

    @JsonProperty
    var count: Int?
) {
    constructor(): this(null, null)
}
