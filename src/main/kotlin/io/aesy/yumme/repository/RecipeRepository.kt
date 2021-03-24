package io.aesy.yumme.repository

import io.aesy.yumme.entity.Recipe
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface RecipeRepository: CrudRepository<Recipe, Long> {
    @Query("SELECT recipe FROM Recipe recipe WHERE recipe.public IS TRUE")
    fun findAllPublic(pageable: Pageable = Pageable.unpaged()): List<Recipe>
}
