package io.aesy.yumme.controller

import io.aesy.yumme.auth.AuthorizedUser
import io.aesy.yumme.conversion.ResponseBodyType
import io.aesy.yumme.dto.RecipeDto
import io.aesy.yumme.entity.Permission.Companion.READ_OWN_USER
import io.aesy.yumme.entity.Permission.Companion.WRITE_OWN_USER
import io.aesy.yumme.entity.Recipe
import io.aesy.yumme.entity.User
import io.aesy.yumme.exception.ResourceNotFound
import io.aesy.yumme.request.CreateRecipeRequest
import io.aesy.yumme.request.UpdateRecipeRequest
import io.aesy.yumme.service.RecipeService
import org.apache.shiro.authz.annotation.*
import org.modelmapper.ModelMapper
import org.springframework.http.HttpStatus
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
    @RequiresAuthentication
    @GetMapping("/recent")
    @Transactional
    @ResponseBodyType(type = RecipeDto::class)
    fun getRecentRecipes(
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): List<Recipe> {
        val maxLimit = 100

        return recipeService.getRecent(min(limit, maxLimit))
    }

    @RequiresAuthentication
    @GetMapping("/popular")
    @Transactional
    @ResponseBodyType(type = RecipeDto::class)
    fun getPopularRecipes(
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): List<Recipe> {
        val maxLimit = 100

        return recipeService.getPopular(min(limit, maxLimit))
    }

    @RequiresAuthentication
    @RequiresPermissions(READ_OWN_USER)
    @GetMapping
    @Transactional
    @ResponseBodyType(type = RecipeDto::class)
    fun getAllRecipes(
        @AuthorizedUser user: User
    ): List<Recipe> {
        throw NotImplementedError()

        // return recipeService.getAll()
    }

    @RequiresAuthentication
    @RequiresPermissions(READ_OWN_USER)
    @GetMapping("{id}")
    @Transactional
    @ResponseBodyType(type = RecipeDto::class)
    fun getRecipeById(
        @AuthorizedUser user: User,
        @PathVariable("id") id: Long
    ): Recipe {
        return recipeService.getById(id)
            .filter { it.author.id == user.id }
            .orElseThrow { ResourceNotFound() }
    }

    @RequiresAuthentication
    @RequiresPermissions(WRITE_OWN_USER)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    @ResponseBodyType(type = RecipeDto::class)
    fun createRecipe(
        @AuthorizedUser user: User,
        @Valid @RequestBody request: CreateRecipeRequest
    ): Recipe {
        request.author = user

        val recipe = modelMapper.map(request, Recipe::class.java)

        return recipeService.save(recipe)
    }

    @RequiresAuthentication
    @RequiresPermissions(WRITE_OWN_USER)
    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    @ResponseBodyType(type = RecipeDto::class)
    fun replaceRecipe(
        @AuthorizedUser user: User,
        @PathVariable("id") id: Long,
        @Valid @RequestBody request: CreateRecipeRequest
    ): Recipe {
        recipeService.getById(id)
            .filter { it.author.id == user.id }
            .orElseThrow { ResourceNotFound() }

        request.author = user

        val recipe = modelMapper.map(request, Recipe::class.java)

        return recipeService.save(recipe)
    }

    @RequiresAuthentication
    @RequiresPermissions(WRITE_OWN_USER)
    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    @ResponseBodyType(type = RecipeDto::class)
    fun updateRecipe(
        @AuthorizedUser user: User,
        @PathVariable("id") id: Long,
        @Valid @RequestBody request: UpdateRecipeRequest
    ): Recipe {
        recipeService.getById(id)
            .filter { it.author.id == user.id }
            .orElseThrow { ResourceNotFound() }

        throw NotImplementedError()
    }

    @RequiresAuthentication
    @RequiresPermissions(WRITE_OWN_USER)
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    fun deleteRecipeById(
        @AuthorizedUser user: User,
        @PathVariable("id") id: Long
    ) {
        val recipe = recipeService.getById(id)
            .filter { it.author.id == user.id }
            .orElseThrow { ResourceNotFound() }

        recipeService.delete(recipe)
    }
}
