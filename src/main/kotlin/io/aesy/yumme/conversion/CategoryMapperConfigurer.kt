package io.aesy.yumme.conversion

import io.aesy.yumme.entity.Category
import org.modelmapper.ModelMapper
import org.springframework.stereotype.Component

@Component
class CategoryMapperConfigurer: MapperConfigurer {
    override fun configure(mapper: ModelMapper) {
        mapper.typeMap(Category::class.java, String::class.java)
            .setConverter {
                it.source.name
            }
    }
}
