package io.aesy.yumme.dto

import com.fasterxml.jackson.annotation.JsonProperty

@Dto
class CollectionDto(
    @JsonProperty
    var id: Long?,

    @JsonProperty
    var title: String?,

    @JsonProperty
    var recipes: MutableSet<Long> = mutableSetOf()
)
