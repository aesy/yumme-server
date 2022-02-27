package io.aesy.yumme.service

import io.aesy.yumme.dto.RatingSummaryDto
import io.aesy.yumme.entity.*
import io.aesy.yumme.repository.RatingRepository
import io.aesy.yumme.util.Doubles.roundToDouble
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

@Service
class RatingService(
    private val ratingRepository: RatingRepository
) {
    @Cacheable("rating-summary", key = "#recipe.id")
    @Transactional
    fun getRatingSummary(recipe: Recipe): RatingSummaryDto {
        var average = ratingRepository.findAverageByRecipe(recipe).roundToDouble(2)
        val count = ratingRepository.findCountByRecipe(recipe)

        if (average.isNaN()) {
            average = 0.0
        }

        return RatingSummaryDto(average, count)
    }

    @Transactional
    fun getAllByRecipe(recipe: Recipe): List<Rating> {
        return ratingRepository.findAllByRecipe(recipe)
            .toList()
    }

    @Transactional
    fun getByRecipeAndUser(recipe: Recipe, user: User): Optional<Rating> {
        return ratingRepository.findByRecipeAndUser(recipe, user)
    }

    @CacheEvict("rating-summary", key = "#recipe.id")
    @Transactional
    fun rateAsUser(user: User, recipe: Recipe, score: Int): Rating {
        return getByRecipeAndUser(recipe, user)
            .orElseGet { Rating(recipe = recipe, user = user) }
            .also { it.score = score }
            .run(ratingRepository::save)
    }
}
