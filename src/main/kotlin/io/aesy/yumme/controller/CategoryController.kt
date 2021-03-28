package io.aesy.yumme.controller

import io.aesy.yumme.dto.CategoryDto
import io.aesy.yumme.exception.ResourceNotFound
import io.aesy.yumme.service.CategoryService
import io.aesy.yumme.service.RecipeService
import io.aesy.yumme.util.ModelMapper.map
import io.swagger.v3.oas.annotations.tags.Tag
import org.modelmapper.ModelMapper
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@Tag(name = "Category")
@RestController
class CategoryController(
    private val recipeService: RecipeService,
    private val categoryService: CategoryService,
    private val mapper: ModelMapper
) {
    @GetMapping("/category")
    @Transactional
    fun listCategories(
        @RequestParam(required = false, defaultValue = "0") offset: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): List<CategoryDto> {
        return categoryService.getAll()
            .asSequence()
            .sortedBy { it.name }
            .map { mapper.map<CategoryDto>(it) }
            .toList()
    }

    @GetMapping("/category/{id}")
    @Transactional
    fun inspectCategoryById(
        @PathVariable(required = true, value = "id") id: Long
    ): CategoryDto {
        return categoryService.getById(id)
            .map { mapper.map<CategoryDto>(it) }
            .orElseThrow { ResourceNotFound() }
    }

    @GetMapping("/recipe/{id}/category")
    @Transactional
    @Tag(name = "Recipe")
    fun listCategoriesByRecipe(
        @PathVariable(required = true, value = "id") id: Long,
        @RequestParam(required = false, defaultValue = "0") offset: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): List<CategoryDto> {
        val recipe = recipeService.getById(id)
            .orElseThrow { ResourceNotFound() }

        return categoryService.getAllByRecipe(recipe)
            .asSequence()
            .sortedBy { it.name }
            .map { mapper.map<CategoryDto>(it) }
            .toList()
    }
}
