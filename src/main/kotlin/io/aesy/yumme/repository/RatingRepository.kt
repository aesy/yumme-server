package io.aesy.yumme.repository

import io.aesy.yumme.entity.Rating
import io.aesy.yumme.entity.Recipe
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface RatingRepository: CrudRepository<Rating, Long> {
    fun findAllByRecipe(recipe: Recipe): Set<Rating>

    @Query("SELECT COALESCE(AVG(rating.score), 0) FROM Rating rating WHERE rating.recipe = :recipe")
    fun getAverageByRecipe(@Param("recipe") recipe: Recipe): Double

    @Query("SELECT COUNT(*) FROM Rating rating WHERE rating.recipe = :recipe")
    fun getCountByRecipe(recipe: Recipe): Long
}
