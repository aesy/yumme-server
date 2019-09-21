package io.aesy.food.repository

import io.aesy.food.entity.Rating
import io.aesy.food.entity.Recipe
import org.springframework.data.repository.CrudRepository

interface RatingRepository: CrudRepository<Rating, Long> {
    fun findAllByRecipe(recipe: Recipe): Set<Rating>
}
