package io.aesy.yumme.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.aesy.yumme.converter.DurationLongJacksonConverter
import io.aesy.yumme.converter.LongDurationJacksonConverter
import org.hibernate.validator.constraints.Length
import java.time.Duration
import javax.validation.constraints.*

@Dto
class UpdateRecipeRequest(
    @field:Length(max = 128)
    @field:JsonProperty("title")
    var title: String? = null,
    @field:Length(max = 512)
    @field:JsonProperty("description")
    var description: String? = null,
    @field:JsonProperty("public")
    var public: Boolean? = null,
    @field:JsonProperty("prep_time")
    @field:JsonSerialize(converter = DurationLongJacksonConverter::class)
    @field:JsonDeserialize(converter = LongDurationJacksonConverter::class)
    var prepTime: Duration? = null,
    @field:JsonProperty("cook_time")
    @field:JsonSerialize(converter = DurationLongJacksonConverter::class)
    @field:JsonDeserialize(converter = LongDurationJacksonConverter::class)
    var cookTime: Duration? = null,
    @field:Min(1)
    @field:JsonProperty("yield")
    var yield: Int? = null,
) {
    @field:JsonProperty("directions")
    var directions: MutableList<String> = mutableListOf()

    @field:JsonProperty("tags")
    var tags: MutableSet<String> = mutableSetOf()

    @field:JsonProperty("categories")
    var categories: MutableSet<String> = mutableSetOf()

    @field:JsonProperty("ingredients")
    var ingredients: MutableSet<String> = mutableSetOf()
}
