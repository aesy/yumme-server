package io.aesy.yumme.controller

import io.aesy.yumme.conversion.ResponseBodyType
import io.aesy.yumme.dto.CategoryDto
import io.aesy.yumme.entity.Category
import io.aesy.yumme.exception.ResourceNotFound
import io.aesy.yumme.service.CategoryService
import io.aesy.yumme.service.RecipeService
import io.aesy.yumme.util.getLogger
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
class CategoryController(
    private val recipeService: RecipeService,
    private val categoryService: CategoryService
) {
    companion object {
        private val logger = getLogger()
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
