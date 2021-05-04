package io.aesy.yumme.mapper

import io.aesy.yumme.dto.CategoryDto
import io.aesy.yumme.entity.Category
import org.springframework.stereotype.Service

@Service
class CategoryMapper {
    fun toDto(category: Category): CategoryDto = CategoryDto(
        name = category.name
    )
}
