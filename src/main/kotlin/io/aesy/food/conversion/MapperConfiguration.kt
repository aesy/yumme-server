package io.aesy.food.config

import io.aesy.food.conversion.MapperConfigurer
import org.modelmapper.ModelMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ModelMapperConfiguration(
    private val configurers: List<MapperConfigurer>
) {
    @Bean
    fun modelMapper(): ModelMapper {
        val mapper = ModelMapper().apply {
            configuration.fieldAccessLevel = org.modelmapper.config.Configuration.AccessLevel.PRIVATE
            configuration.isFieldMatchingEnabled = true
        }

        for (configurer in configurers) {
            configurer.configure(mapper)
        }

        return mapper
    }
}
