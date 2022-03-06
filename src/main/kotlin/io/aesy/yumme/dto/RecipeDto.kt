package io.aesy.yumme.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.aesy.yumme.converter.DurationLongSecondsJacksonConverter
import io.aesy.yumme.converter.LongSecondsDurationJacksonConverter
import java.time.Duration

@Dto
class RecipeDto(
    @JsonProperty
    var id: Long?,

    @JsonProperty
    var title: String?,

    @JsonProperty
    var description: String?,

    @JsonProperty
    var directions: String?,

    @JsonProperty
    var rating: RatingSummaryDto?,

    @JsonProperty("prep_time")
    @JsonSerialize(converter = DurationLongSecondsJacksonConverter::class)
    @JsonDeserialize(converter = LongSecondsDurationJacksonConverter::class)
    var prepTime: Duration? = Duration.ofMinutes(10),

    @JsonProperty("cook_time")
    @JsonSerialize(converter = DurationLongSecondsJacksonConverter::class)
    @JsonDeserialize(converter = LongSecondsDurationJacksonConverter::class)
    var cookTime: Duration? = Duration.ofMinutes(20),

    @JsonProperty
    var yield: Int? = 4,

    @JsonProperty
    var tags: MutableSet<String> = mutableSetOf(),

    @JsonProperty
    var categories: MutableSet<String> = mutableSetOf(),

    @JsonProperty
    var images: MutableSet<String> = mutableSetOf(),

    @JsonProperty
    var ingredients: MutableSet<IngredientDto> = mutableSetOf()
)
