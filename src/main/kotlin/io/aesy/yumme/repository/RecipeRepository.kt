package io.aesy.yumme.repository

import io.aesy.yumme.entity.Recipe
import io.aesy.yumme.entity.User
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface RecipeRepository: CrudRepository<Recipe, Long>, JpaSpecificationExecutor<Recipe> {
    fun findAllByAuthor(user: User, pageable: Pageable = Pageable.unpaged()): List<Recipe>

    @Query("SELECT recipe FROM Recipe recipe WHERE recipe.public IS TRUE AND recipe.author = :user")
    fun findAllPublicByAuthor(user: User, pageable: Pageable = Pageable.unpaged()): List<Recipe>

    @Query("SELECT recipe FROM Recipe recipe WHERE recipe.public IS TRUE")
    fun findAllPublic(pageable: Pageable = Pageable.unpaged()): List<Recipe>
}
