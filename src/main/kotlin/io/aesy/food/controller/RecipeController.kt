package io.aesy.food.controller

import io.aesy.food.conversion.ResponseBodyType
import io.aesy.food.dto.RecipeDto
import io.aesy.food.entity.Recipe
import io.aesy.food.exception.ResourceNotFound
import io.aesy.food.request.CreateRecipeRequest
import io.aesy.food.request.UpdateRecipeRequest
import io.aesy.food.service.RecipeService
import org.modelmapper.ModelMapper
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.transaction.Transactional
import javax.validation.Valid

@RestController
@RequestMapping("recipe")
class RecipeController(
    private val recipeService: RecipeService,
    private val modelMapper: ModelMapper
) {
    @GetMapping("/recent")
    @Transactional
    @ResponseBodyType(type = RecipeDto::class)
    fun recent(
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): List<Recipe> {
        val maxLimit = 100

        return recipeService.getAll()
            .asSequence()
            .sortedByDescending { it.id }
            .take(Math.min(limit, maxLimit))
            .toList()
    }

    @GetMapping("/popular")
    @Transactional
    @ResponseBodyType(type = RecipeDto::class)
    fun popular(
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): List<Recipe> {
        val maxLimit = 100

        return recipeService.getAll()
            .asSequence()
            .sortedByDescending { recipe ->
                recipe.ratings.sumBy { it.score } / recipe.ratings.size.toDouble()
            }
            .take(Math.min(limit, maxLimit))
            .toList()
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
