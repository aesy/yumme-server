package io.aesy.yumme.mapper

import io.aesy.yumme.dto.TagDto
import io.aesy.yumme.entity.Tag
import org.springframework.stereotype.Service

@Service
class TagMapper {
    fun toDto(tag: Tag): TagDto = TagDto(
        name = tag.name
    )
}
