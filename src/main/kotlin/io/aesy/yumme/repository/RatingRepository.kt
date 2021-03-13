package io.aesy.yumme.repository

import io.aesy.yumme.entity.Rating
import io.aesy.yumme.entity.Recipe
import org.springframework.data.repository.CrudRepository

interface RatingRepository: CrudRepository<Rating, Long> {
    fun findAllByRecipe(recipe: Recipe): Set<Rating>
}
