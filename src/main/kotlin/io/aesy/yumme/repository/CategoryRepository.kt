package io.aesy.yumme.repository

import io.aesy.yumme.entity.Category
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import java.util.*

interface CategoryRepository: CrudRepository<Category, Long> {
    fun findAll(pageable: Pageable = Pageable.unpaged()): List<Category>
    fun findByName(name: String): Optional<Category>
}
