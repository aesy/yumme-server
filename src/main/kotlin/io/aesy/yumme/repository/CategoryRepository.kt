package io.aesy.yumme.repository

import io.aesy.yumme.entity.Category
import org.springframework.data.repository.CrudRepository
import java.util.*

interface CategoryRepository: CrudRepository<Category, Long> {
    fun findByName(name: String): Optional<Category>
}
