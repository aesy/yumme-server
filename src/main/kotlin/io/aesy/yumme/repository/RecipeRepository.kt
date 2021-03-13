package io.aesy.yumme.repository

import io.aesy.yumme.entity.Recipe
import org.springframework.data.repository.CrudRepository

interface RecipeRepository: CrudRepository<Recipe, Long>
