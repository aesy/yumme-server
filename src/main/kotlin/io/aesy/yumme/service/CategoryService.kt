package io.aesy.yumme.service

import io.aesy.yumme.entity.Category
import io.aesy.yumme.entity.Recipe
import io.aesy.yumme.repository.CategoryRepository
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository
) {
    @Transactional
    fun getAll(): List<Category> {
        return categoryRepository.findAll()
            .toList()
    }

    @Transactional
    fun getAllByRecipe(recipe: Recipe): List<Category> {
        return recipe.categories.toList()
    }

    @Transactional
    fun getById(id: Long): Optional<Category> {
        return categoryRepository.findById(id)
    }

    @Transactional
    fun getByName(name: String): Optional<Category> {
        return categoryRepository.findByName(name)
    }

    @Transactional
    fun save(tag: Category): Category {
        return categoryRepository.save(tag)
    }

    @Transactional
    fun delete(tag: Category): Boolean {
        categoryRepository.delete(tag)

        return true
    }

    @Transactional
    fun delete(id: Long): Boolean {
        categoryRepository.deleteById(id)

        return true
    }
}
