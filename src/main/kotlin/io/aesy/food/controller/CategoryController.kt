package io.aesy.food.controller

import io.aesy.food.conversion.ResponseBodyType
import io.aesy.food.dto.CategoryDto
import io.aesy.food.entity.Category
import io.aesy.food.exception.ResourceNotFound
import io.aesy.food.service.CategoryService
import io.aesy.food.service.RecipeService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
class CategoryController(
    private val recipeService: RecipeService,
    private val categoryService: CategoryService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(CategoryController::class.java)
    }

    @GetMapping("/category")
    @Transactional
    @ResponseBodyType(type = CategoryDto::class)
    fun list(
        @RequestParam(required = false, defaultValue = "0") offset: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): List<Category> {
        return categoryService.getAll()
            .asSequence()
            .sortedBy { it.name }
            .toList()
    }

    @GetMapping("/recipe/{id}/category")
    @Transactional
    @ResponseBodyType(type = CategoryDto::class)
    fun listByRecipe(
        @PathVariable(required = true, value = "id") id: Long,
        @RequestParam(required = false, defaultValue = "0") offset: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): List<Category> {
        val recipe = recipeService.getById(id)
            .orElseThrow { ResourceNotFound() }

        return categoryService.getAllByRecipe(recipe)
            .asSequence()
            .sortedBy { it.name }
            .toList()
    }
}
