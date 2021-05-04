package io.aesy.yumme.mapper

import io.aesy.yumme.dto.CollectionDto
import io.aesy.yumme.entity.Collection
import org.springframework.stereotype.Service

@Service
class CollectionMapper {
    fun toDto(collection: Collection): CollectionDto = CollectionDto(
        id = collection.id,
        title = collection.title,
        recipes = collection.recipes.map { it.id!! }.toMutableSet()
    )
}
