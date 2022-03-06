package io.aesy.yumme.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.aesy.yumme.converter.DurationLongSecondsJacksonConverter
import io.aesy.yumme.converter.LongSecondsDurationJacksonConverter
import io.aesy.yumme.validation.MaxDuration
import io.aesy.yumme.validation.MinDuration
import org.hibernate.validator.constraints.Length
import java.time.Duration
import java.time.temporal.ChronoUnit
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
    var directions: String?,
    @field:NotNull
    @field:JsonProperty("public")
    var public: Boolean?,
    @field:NotNull
    @field:MaxDuration(1, ChronoUnit.DAYS)
    @field:MinDuration(0, ChronoUnit.SECONDS)
    @field:JsonProperty("prep_time")
    @field:JsonSerialize(converter = DurationLongSecondsJacksonConverter::class)
    @field:JsonDeserialize(converter = LongSecondsDurationJacksonConverter::class)
    var prepTime: Duration?,
    @field:NotNull
    @field:MaxDuration(1, ChronoUnit.DAYS)
    @field:MinDuration(0, ChronoUnit.SECONDS)
    @field:JsonProperty("cook_time")
    @field:JsonSerialize(converter = DurationLongSecondsJacksonConverter::class)
    @field:JsonDeserialize(converter = LongSecondsDurationJacksonConverter::class)
    var cookTime: Duration?,
    @field:NotNull
    @field:Max(100)
    @field:Min(1)
    @field:JsonProperty("yield")
    var yield: Int?,
) {
    @field:JsonProperty("tags")
    var tags: MutableSet<String> = mutableSetOf()

    @field:JsonProperty("categories")
    var categories: MutableSet<String> = mutableSetOf()

    @field:JsonProperty("ingredients")
    var ingredients: MutableSet<String> = mutableSetOf()
}
