package io.aesy.yumme.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.aesy.yumme.conversion.DurationLongJacksonConverter
import io.aesy.yumme.conversion.LongDurationJacksonConverter
import io.aesy.yumme.entity.User
import org.hibernate.validator.constraints.Length
import java.time.Duration
import javax.validation.constraints.*

data class CreateRecipeRequest(
    @field:NotEmpty
    @field:Length(max = 128)
    @field:JsonProperty("title")
    var title: String?,
    @field:NotEmpty
    @field:Length(max = 4096)
    @field:JsonProperty("description")
    var description: String?,
    @field:NotNull
    @field:JsonProperty("public")
    var public: Boolean?,
    @field:NotNull
    @field:JsonProperty("completion_time")
    @field:JsonSerialize(converter = DurationLongJacksonConverter::class)
    @field:JsonDeserialize(converter = LongDurationJacksonConverter::class)
    var completionTime: Duration?
) {
    @field:NotNull
    @field:JsonProperty("tags")
    var tags: Set<String> = setOf()

    @field:NotNull
    @field:JsonProperty("categories")
    var categories: Set<String> = setOf()

    // Set by endpoint handler
    @field:Null
    var author: User? = null
}
