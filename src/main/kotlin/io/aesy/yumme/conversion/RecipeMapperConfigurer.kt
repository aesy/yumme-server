package io.aesy.yumme.conversion

import io.aesy.yumme.dto.RecipeDto
import io.aesy.yumme.entity.Recipe
import io.aesy.yumme.request.CreateRecipeRequest
import io.aesy.yumme.service.RatingService
import org.modelmapper.ModelMapper
import org.springframework.stereotype.Component

@Component
class RecipeMapperConfigurer(
    private val ratingService: RatingService
): MapperConfigurer {
    override fun configure(mapper: ModelMapper) {
        mapper.typeMap(CreateRecipeRequest::class.java, Recipe::class.java)

        mapper.typeMap(Recipe::class.java, RecipeDto::class.java)
            .setPostConverter { context ->
                val recipe = context.source

                context.destination.apply {
                    rating = ratingService.getRatingSummary(recipe)
                }
            }
    }
}
