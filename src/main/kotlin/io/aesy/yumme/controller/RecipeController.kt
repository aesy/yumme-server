package io.aesy.yumme.controller

import io.aesy.yumme.auth.AuthorizedUser
import io.aesy.yumme.dto.RecipeDto
import io.aesy.yumme.entity.Permission.Companion.READ_OWN_USER
import io.aesy.yumme.entity.Permission.Companion.WRITE_OWN_USER
import io.aesy.yumme.entity.Recipe
import io.aesy.yumme.entity.User
import io.aesy.yumme.exception.ResourceNotFound
import io.aesy.yumme.request.CreateRecipeRequest
import io.aesy.yumme.request.UpdateRecipeRequest
import io.aesy.yumme.service.RecipeService
import io.aesy.yumme.util.ModelMapper.map
import io.swagger.v3.oas.annotations.tags.Tag
import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.apache.shiro.authz.annotation.RequiresPermissions
import org.modelmapper.ModelMapper
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional
import javax.validation.Valid
import kotlin.math.min

@Tag(name = "Recipe")
@RestController
@RequestMapping("recipe")
class RecipeController(
    private val recipeService: RecipeService,
    private val mapper: ModelMapper
) {
    @RequiresAuthentication
    @GetMapping("/recent")
    @Transactional
    fun listRecentRecipes(
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): List<RecipeDto> {
        val maxLimit = 100

        return recipeService.getRecent(min(limit, maxLimit))
            .map { mapper.map(it) }
    }

    @RequiresAuthentication
    @GetMapping("/popular")
    @Transactional
    fun listPopularRecipes(
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): List<RecipeDto> {
        val maxLimit = 100

        return recipeService.getPopular(min(limit, maxLimit))
            .map { mapper.map(it) }
    }

    @RequiresAuthentication
    @RequiresPermissions(READ_OWN_USER)
    @GetMapping
    @Transactional
    fun listRecipes(
        @AuthorizedUser user: User
    ): List<RecipeDto> {
        throw NotImplementedError()

        // return recipeService.getAll()
    }

    @RequiresAuthentication
    @RequiresPermissions(READ_OWN_USER)
    @GetMapping("{id}")
    @Transactional
    fun inspectRecipeById(
        @AuthorizedUser user: User,
        @PathVariable("id") id: Long
    ): RecipeDto {
        return recipeService.getById(id)
            .filter { it.author.id == user.id }
            .map { mapper.map<RecipeDto>(it) }
            .orElseThrow { ResourceNotFound() }
    }

    @RequiresAuthentication
    @RequiresPermissions(WRITE_OWN_USER)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    fun createRecipe(
        @AuthorizedUser user: User,
        @Valid @RequestBody request: CreateRecipeRequest
    ): RecipeDto {
        request.author = user

        val recipe = mapper.map(request, Recipe::class.java)

        recipeService.save(recipe)

        return mapper.map(recipe)
    }

    @RequiresAuthentication
    @RequiresPermissions(WRITE_OWN_USER)
    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    fun replaceRecipe(
        @AuthorizedUser user: User,
        @PathVariable("id") id: Long,
        @Valid @RequestBody request: CreateRecipeRequest
    ): RecipeDto {
        recipeService.getById(id)
            .filter { it.author.id == user.id }
            .orElseThrow { ResourceNotFound() }

        request.author = user

        val recipe = mapper.map(request, Recipe::class.java)

        recipeService.save(recipe)

        return mapper.map(recipe)
    }

    @RequiresAuthentication
    @RequiresPermissions(WRITE_OWN_USER)
    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    fun updateRecipe(
        @AuthorizedUser user: User,
        @PathVariable("id") id: Long,
        @Valid @RequestBody request: UpdateRecipeRequest
    ): RecipeDto {
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
