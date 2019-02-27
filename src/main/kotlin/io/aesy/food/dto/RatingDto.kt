package io.aesy.food.dto

import com.fasterxml.jackson.annotation.JsonProperty

@Dto
data class RatingDto(
    @JsonProperty
    var average: Double?,

    @JsonProperty
    var count: Int?
)
