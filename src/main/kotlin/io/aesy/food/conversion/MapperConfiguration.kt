package io.aesy.food.config

import io.aesy.food.dto.RatingDto
import io.aesy.food.dto.RecipeDto
import io.aesy.food.entity.Recipe
import org.modelmapper.ModelMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class ModelMapperConfiguration {
    @Bean
    fun modelMapper(): ModelMapper {
        val modelMapper = ModelMapper()

        modelMapper.createTypeMap(Recipe::class.java, RecipeDto::class.java)
                   .setPostConverter { context ->
                       val source = context.source.ratings
                       val average = source
                           .asSequence()
                           .map { it.score }
                           .average()
                       val precision = 2
                       val pow = Math.pow(10.0, precision.toDouble())
                       val formattedAverage = Math.round(average * pow) / pow

                       context.destination.apply {
                           ratings = RatingDto(formattedAverage, source.size)
                       }
                   }

        return modelMapper
    }
}
