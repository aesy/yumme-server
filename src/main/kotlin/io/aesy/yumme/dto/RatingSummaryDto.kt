package io.aesy.yumme.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

@Dto
class RatingSummaryDto(
    @JsonProperty
    var average: Double?,

    @JsonProperty
    var count: Long?
): Serializable
