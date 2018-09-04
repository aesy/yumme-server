package io.aesy.food.service

import io.aesy.food.entity.Recipe
import io.aesy.food.repository.RecipeRepository
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
    fun getById(id: Long): Optional<Recipe> {
        return recipeRepository.findById(id)
    }

    @Transactional
    fun getByUuid(uuid: UUID): Optional<Recipe> {
        // return recipeRepository.findRecipeByUuid(uuid)
        throw NotImplementedError()
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
    fun delete(id: Long): Boolean {
        recipeRepository.deleteById(id)

        return true
    }
}
