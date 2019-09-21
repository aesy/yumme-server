package io.aesy.food.repository

import io.aesy.food.entity.Recipe
import io.aesy.food.entity.Tag
import org.springframework.data.repository.CrudRepository
import java.util.*

interface TagRepository: CrudRepository<Tag, Long> {
    fun findAllByRecipe(recipe: Recipe): Set<Tag>
    fun findByName(name: String): Optional<Tag>
}
