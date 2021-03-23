package io.aesy.yumme.dto

import com.fasterxml.jackson.annotation.JsonProperty

@Dto
data class RecipeDto(
    @JsonProperty
    var id: Long?,

    @JsonProperty
    var title: String?,

    @JsonProperty
    var description: String?,

    @JsonProperty
    var rating: RatingDto?,

    @JsonProperty
    var tags: Set<String> = setOf(),

    @JsonProperty
    var categories: Set<String> = setOf()
)
