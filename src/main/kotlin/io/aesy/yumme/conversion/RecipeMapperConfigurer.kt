package io.aesy.yumme.conversion

import io.aesy.yumme.dto.RatingDto
import io.aesy.yumme.dto.RecipeDto
import io.aesy.yumme.entity.Recipe
import io.aesy.yumme.repository.RatingRepository
import io.aesy.yumme.util.Doubles.round
import org.modelmapper.ModelMapper
import org.springframework.stereotype.Component

@Component
class RecipeMapperConfigurer(
    private val ratingRepository: RatingRepository
): MapperConfigurer {
    override fun configure(mapper: ModelMapper) {
        mapper.typeMap(Recipe::class.java, RecipeDto::class.java)
            .setPostConverter { context ->
                val recipe = context.source
                val average = ratingRepository.getAverageByRecipe(recipe).round(2)
                val count = ratingRepository.getCountByRecipe(recipe)

                context.destination.apply {
                    rating = RatingDto(average, count)
                }
            }
    }
}
