package io.aesy.food.repository

import io.aesy.food.entity.Category
import org.springframework.data.repository.CrudRepository
import java.util.*

interface CategoryRepository: CrudRepository<Category, Long> {
    fun findByName(name: String): Optional<Category>
}
