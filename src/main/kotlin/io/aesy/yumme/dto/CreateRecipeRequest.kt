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
class CreateRecipeRequest(
    @field:NotEmpty
    @field:Length(max = 128)
    @field:JsonProperty("title")
    var title: String?,
    @field:NotEmpty
    @field:Length(max = 512)
    @field:JsonProperty("description")
    var description: String?,
    @field:NotEmpty
    @field:JsonProperty("directions")
    var directions: MutableList<String>,
    @field:NotNull
    @field:JsonProperty("public")
    var public: Boolean?,
    @field:NotNull
    @field:JsonProperty("prep_time")
    @field:JsonSerialize(converter = DurationLongJacksonConverter::class)
    @field:JsonDeserialize(converter = LongDurationJacksonConverter::class)
    var prepTime: Duration?,
    @field:NotNull
    @field:JsonProperty("cook_time")
    @field:JsonSerialize(converter = DurationLongJacksonConverter::class)
    @field:JsonDeserialize(converter = LongDurationJacksonConverter::class)
    var cookTime: Duration?,
    @field:NotNull
    @field:Min(1)
    @field:JsonProperty("yield")
    var yield: Int?,
) {
    @field:NotNull
    @field:JsonProperty("tags")
    var tags: MutableSet<String> = mutableSetOf()

    @field:NotNull
    @field:JsonProperty("categories")
    var categories: MutableSet<String> = mutableSetOf()

    @field:NotNull
    @field:JsonProperty("ingredients")
    var ingredients: MutableSet<String> = mutableSetOf()
}
