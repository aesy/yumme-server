package io.aesy.yumme.dto

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotNull

@Dto
class UpdateCollectionRequest(
    @field:NotNull
    @field:JsonProperty("title")
    var title: String?
) {
    @field:NotNull
    @field:JsonProperty("recipes")
    var recipes: MutableSet<Long> = mutableSetOf()
}
