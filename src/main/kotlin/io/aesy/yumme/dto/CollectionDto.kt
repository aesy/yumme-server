package io.aesy.yumme.dto

import com.fasterxml.jackson.annotation.JsonProperty

@Dto
data class CollectionDto(
    @JsonProperty
    val title: String,

    @JsonProperty
    val recipes: List<String> = arrayListOf()
)
