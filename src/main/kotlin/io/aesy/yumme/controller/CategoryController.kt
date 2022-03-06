package io.aesy.yumme.controller

import io.aesy.yumme.auth.AuthorizedUser
import io.aesy.yumme.dto.CategoryDto
import io.aesy.yumme.entity.*
import io.aesy.yumme.exception.ResourceAlreadyExists
import io.aesy.yumme.exception.ResourceNotFound
import io.aesy.yumme.mapper.CategoryMapper
import io.aesy.yumme.service.CategoryService
import io.aesy.yumme.service.RecipeService
import io.aesy.yumme.util.AccessControl.canRead
import io.swagger.v3.oas.annotations.tags.Tag
import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.apache.shiro.authz.annotation.RequiresRoles
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional
import kotlin.math.min

@Tag(name = "Category")
@RestController
@RequestMapping(
    consumes = [MediaType.APPLICATION_JSON_VALUE, MediaType.ALL_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
@Validated
class CategoryController(
    private val recipeService: RecipeService,
    private val categoryService: CategoryService,
    private val mapper: CategoryMapper
) {
    @RequiresAuthentication
    @RequiresRoles(Role.ADMIN)
    @PostMapping("/category")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    fun createCategory(
        @AuthorizedUser user: User,
        @RequestParam("name") name: String
    ): CategoryDto {
        categoryService.getByName(name)
            .ifPresent { throw ResourceAlreadyExists() }

        val category = categoryService.save(Category(name = name))

        return mapper.toDto(category)
    }

    @RequiresAuthentication
    @GetMapping("/category")
    @Transactional
    fun listAllCategories(
        @RequestParam(required = false, defaultValue = "0") offset: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): List<CategoryDto> {
        val maxLimit = 100

        return categoryService.getAll(min(limit, maxLimit), offset)
            .map(mapper::toDto)
    }

    @RequiresAuthentication
    @GetMapping("/recipe/{id}/category")
    @Transactional
    @Tag(name = "Recipe")
    fun listCategoriesByRecipe(
        @AuthorizedUser user: User,
        @PathVariable("id") id: Long
    ): List<CategoryDto> {
        val recipe = recipeService.getById(id)
            .filter { user.canRead(it) }
            .orElseThrow { ResourceNotFound.recipe(id) }

        return categoryService.getAllByRecipe(recipe)
            .map(mapper::toDto)
    }

    @RequiresAuthentication
    @RequiresRoles(Role.ADMIN)
    @DeleteMapping("/category/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    fun deleteCategoryByName(
        @AuthorizedUser user: User,
        @PathVariable("name") name: String
    ) {
        categoryService.getByName(name)
            .ifPresent(categoryService::delete)
    }
}
