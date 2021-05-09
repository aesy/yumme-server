package io.aesy.yumme.service

import io.aesy.yumme.entity.Category
import io.aesy.yumme.entity.Recipe
import io.aesy.yumme.repository.CategoryRepository
import org.springframework.data.domain.*
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository
) {
    @Transactional
    fun getAll(limit: Int = 0, offset: Int = 0): List<Category> {
        val page = if (limit > 0) {
            PageRequest.of(offset, limit)
        } else {
            Pageable.unpaged()
        }

        return categoryRepository.findAll(page)
    }

    @Transactional
    fun getAllByRecipe(recipe: Recipe): List<Category> {
        return recipe.categories.toList()
    }

    @Transactional
    fun getByName(name: String): Optional<Category> {
        return categoryRepository.findByName(name)
    }

    @Transactional
    fun save(tag: Category): Category {
        return categoryRepository.save(tag)
    }
}
