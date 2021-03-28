package io.aesy.yumme.dto

import com.fasterxml.jackson.annotation.JsonProperty

@Dto
data class RatingSummaryDto(
    @JsonProperty
    var average: Double?,

    @JsonProperty
    var count: Long?
)
