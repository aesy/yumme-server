package io.aesy.yumme.repository

import io.aesy.yumme.entity.*
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.util.*

interface RatingRepository: CrudRepository<Rating, Long> {
    fun findAllByRecipe(recipe: Recipe): Set<Rating>
    fun findByRecipeAndUser(recipe: Recipe, user: User): Optional<Rating>

    @Query("SELECT COALESCE(AVG(rating.score), 0) FROM Rating rating WHERE rating.recipe = :recipe")
    fun findAverageByRecipe(@Param("recipe") recipe: Recipe): Double

    @Query("SELECT COUNT(*) FROM Rating rating WHERE rating.recipe = :recipe")
    fun findCountByRecipe(recipe: Recipe): Long
}
