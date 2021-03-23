package io.aesy.yumme.conversion

import io.aesy.yumme.entity.*
import org.modelmapper.ModelMapper
import org.springframework.stereotype.Component

@Component
class TagMapperConfigurer: MapperConfigurer {
    override fun configure(mapper: ModelMapper) {
        mapper.typeMap(Tag::class.java, String::class.java)
            .setConverter {
                it.source.name
            }
    }
}
