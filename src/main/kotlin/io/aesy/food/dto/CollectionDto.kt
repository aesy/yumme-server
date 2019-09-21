package io.aesy.food.dto

import com.fasterxml.jackson.annotation.JsonProperty

@Dto
data class CollectionDto(
    @JsonProperty
    val title: String,

    @JsonProperty
    val recipes: List<RecipeDto> = arrayListOf()
)
