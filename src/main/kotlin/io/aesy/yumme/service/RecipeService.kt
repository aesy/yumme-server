package io.aesy.yumme.service

import io.aesy.yumme.entity.Recipe
import io.aesy.yumme.repository.RecipeRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

@Service
class RecipeService(
    private val recipeRepository: RecipeRepository
) {
    @Transactional
    fun getAll(): List<Recipe> {
        return recipeRepository.findAll()
            .toList()
    }

    @Transactional
    fun getRecent(count: Int): List<Recipe> {
        val sort = Sort.by(Direction.DESC, Recipe::createdAt.name)
        val page = PageRequest.of(0, count, sort)

        return recipeRepository.findAllPublic(page)
            .sortedBy { it.createdAt }
    }

    @Transactional
    fun getPopular(count: Int): List<Recipe> {
        val sort = Sort.by(Direction.DESC, Recipe::createdAt.name) // TODO use popularity
        val page = PageRequest.of(0, count, sort)

        return recipeRepository.findAllPublic(page)
    }

    @Transactional
    fun getById(id: Long): Optional<Recipe> {
        return recipeRepository.findById(id)
    }

    @Transactional
    fun save(recipe: Recipe): Recipe {
        return recipeRepository.save(recipe)
    }

    @Transactional
    fun delete(recipe: Recipe): Boolean {
        recipeRepository.delete(recipe)

        return true
    }

    @Transactional
    fun deleteById(id: Long): Boolean {
        recipeRepository.deleteById(id)

        return true
    }
}
