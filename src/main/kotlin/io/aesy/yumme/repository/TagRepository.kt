package io.aesy.yumme.repository

import io.aesy.yumme.entity.Recipe
import io.aesy.yumme.entity.Tag
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import java.util.*

interface TagRepository: CrudRepository<Tag, Long> {
    fun findByNameAndRecipe(name: String, recipe: Recipe): Optional<Tag>
    fun findAll(pageable: Pageable = Pageable.unpaged()): List<Tag>
    fun findAllByRecipe(recipe: Recipe, pageable: Pageable = Pageable.unpaged()): List<Tag>
}
