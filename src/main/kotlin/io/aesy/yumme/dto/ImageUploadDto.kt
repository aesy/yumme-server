package io.aesy.yumme.dto

import com.fasterxml.jackson.annotation.JsonProperty

@Dto
class ImageUploadDto(
    @JsonProperty
    var name: String?
)
