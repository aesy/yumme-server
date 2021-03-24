package io.aesy.yumme.controller

import io.aesy.yumme.conversion.ResponseBodyType
import io.aesy.yumme.dto.RecipeDto
import io.aesy.yumme.entity.Recipe
import io.aesy.yumme.exception.ResourceNotFound
import io.aesy.yumme.request.CreateRecipeRequest
import io.aesy.yumme.request.UpdateRecipeRequest
import io.aesy.yumme.service.RecipeService
import org.modelmapper.ModelMapper
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional
import javax.validation.Valid
import kotlin.math.min

@RestController
@RequestMapping("recipe")
class RecipeController(
    private val recipeService: RecipeService,
    private val modelMapper: ModelMapper
) {
    @GetMapping("/recent")
    @Transactional
    @ResponseBodyType(type = RecipeDto::class)
    fun getRecentRecipes(
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): List<Recipe> {
        val maxLimit = 100

        return recipeService.getRecent(min(limit, maxLimit))
    }

    @GetMapping("/popular")
    @Transactional
    @ResponseBodyType(type = RecipeDto::class)
    fun getPopularRecipes(
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): List<Recipe> {
        val maxLimit = 100

        return recipeService.getPopular(min(limit, maxLimit))
    }

    @GetMapping
    @Transactional
    @ResponseBodyType(type = RecipeDto::class)
    fun getAllRecipes(
        @CookieValue("access_token", required = false) token: String?
    ): List<Recipe> {
        throw NotImplementedError()

//        return recipeService.getAll()
    }

    @GetMapping("{id}")
    @Transactional
    @ResponseBodyType(type = RecipeDto::class)
    fun getRecipeByUuid(
        @PathVariable("id") id: Int
    ): Recipe {
        return recipeService.getById(id)
            .orElseThrow { ResourceNotFound() }
    }

    @PostMapping
    @Transactional
    fun createRecipe(
        @Valid @RequestBody recipe: CreateRecipeRequest
    ) {
        recipeService.save(modelMapper.map(recipe, Recipe::class.java))
    }

    @PutMapping("{id}")
    @Transactional
    fun replaceRecipe(
        @PathVariable("id") uuid: UUID,
        @Valid @RequestBody recipe: CreateRecipeRequest
    ) {
        recipeService.getByUuid(uuid)
            .orElseThrow { ResourceNotFound() }

        recipeService.save(modelMapper.map(recipe, Recipe::class.java))
    }

    @PatchMapping("{id}")
    @Transactional
    fun updateRecipe(
        @PathVariable("id") uuid: UUID,
        @Valid @RequestBody recipe: UpdateRecipeRequest
    ) {
        recipeService.getByUuid(uuid)
            .orElseThrow { ResourceNotFound() }

        throw NotImplementedError()
    }

    @DeleteMapping("{id}")
    @Transactional
    fun deleteRecipeByUuid(
        @PathVariable("id") uuid: UUID
    ) {
        val recipe = recipeService.getByUuid(uuid)
            .orElseThrow { ResourceNotFound() }

        recipeService.delete(recipe)
    }
}
