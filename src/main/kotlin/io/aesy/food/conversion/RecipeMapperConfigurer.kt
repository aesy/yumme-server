package io.aesy.food.conversion

import io.aesy.food.dto.RatingDto
import io.aesy.food.dto.RecipeDto
import io.aesy.food.entity.Recipe
import io.aesy.food.repository.RatingRepository
import org.modelmapper.ModelMapper
import org.springframework.stereotype.Component
import kotlin.math.pow
import kotlin.math.roundToInt

@Component
class RecipeMapperConfigurer(
    private val ratingRepository: RatingRepository
): MapperConfigurer {
    override fun configure(mapper: ModelMapper) {
        mapper.typeMap(Recipe::class.java, RecipeDto::class.java)
            .setPostConverter { context ->
                val ratings = ratingRepository.findAllByRecipe(context.source)
                val average = ratings
                    .asSequence()
                    .map { it.score }
                    .average()
                val precision = 2
                val pow = 10.0.pow(precision.toDouble())
                val formattedAverage = (average * pow).roundToInt() / pow

                context.destination.apply {
                    rating = RatingDto(formattedAverage, ratings.size)
                }
            }
    }
}
