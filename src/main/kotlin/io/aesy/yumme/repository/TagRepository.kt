package io.aesy.yumme.repository

import io.aesy.yumme.entity.Recipe
import io.aesy.yumme.entity.Tag
import org.springframework.data.repository.CrudRepository
import java.util.*

interface TagRepository: CrudRepository<Tag, Long> {
    fun findAllByRecipe(recipe: Recipe): Set<Tag>
    fun findByName(name: String): Optional<Tag>
}
