package io.aesy.food.repository

import io.aesy.food.entity.Recipe
import org.springframework.data.repository.CrudRepository

interface RecipeRepository: CrudRepository<Recipe, Long>
