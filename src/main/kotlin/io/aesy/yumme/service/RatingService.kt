package io.aesy.yumme.service

import io.aesy.yumme.dto.RatingSummaryDto
import io.aesy.yumme.entity.Rating
import io.aesy.yumme.entity.Recipe
import io.aesy.yumme.repository.RatingRepository
import io.aesy.yumme.util.Doubles.round
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class RatingService(
    private val ratingRepository: RatingRepository
) {
    @Cacheable("rating-summary", key = "#recipe.id")
    @Transactional
    fun getRatingSummary(recipe: Recipe): RatingSummaryDto {
        val average = ratingRepository.getAverageByRecipe(recipe).round(2)
        val count = ratingRepository.getCountByRecipe(recipe)

        return RatingSummaryDto(average, count)
    }

    @Transactional
    fun getAll(): List<Rating> {
        return ratingRepository.findAll()
            .toList()
    }

    @Transactional
    fun getAllByRecipe(recipe: Recipe): List<Rating> {
        return ratingRepository.findAllByRecipe(recipe)
            .toList()
    }

    @CacheEvict("rating-summary", key = "#rating.recipe.id")
    @Transactional
    fun save(rating: Rating): Rating {
        return ratingRepository.save(rating)
    }

    @CacheEvict("rating-summary", key = "#rating.recipe.id")
    @Transactional
    fun delete(rating: Rating): Boolean {
        ratingRepository.delete(rating)

        return true
    }

    @CacheEvict("rating-summary", key = "#rating.recipe.id")
    @Transactional
    fun delete(id: Long): Boolean {
        ratingRepository.deleteById(id)

        return true
    }
}
