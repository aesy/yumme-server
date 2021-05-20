package io.aesy.yumme.controller

import io.aesy.yumme.auth.AuthorizedUser
import io.aesy.yumme.dto.*
import io.aesy.yumme.entity.*
import io.aesy.yumme.exception.ResourceNotFound
import io.aesy.yumme.mapper.RecipeMapper
import io.aesy.yumme.service.*
import io.aesy.yumme.util.AccessControl.canRead
import io.aesy.yumme.util.AccessControl.canWrite
import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import javax.transaction.Transactional
import javax.validation.Valid
import kotlin.math.min

@io.swagger.v3.oas.annotations.tags.Tag(name = "Recipe")
@RestController
@RequestMapping(
    "recipe",
    consumes = [MediaType.APPLICATION_JSON_VALUE, MediaType.ALL_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
@Validated
class RecipeController(
    private val recipeService: RecipeService,
    private val categoryService: CategoryService,
    private val ingredientService: IngredientService,
    private val tagService: TagService,
    private val userService: UserService,
    private val mapper: RecipeMapper
) {
    @RequiresAuthentication
    @GetMapping("/recent")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    fun listRecentRecipes(
        @AuthorizedUser user: User,
        @RequestParam(required = false, defaultValue = "10") limit: Int,
        @RequestParam("user", required = false) userId: Long?
    ): List<RecipeDto> {
        val maxLimit = 100
        val author = if (userId == null) {
            user
        } else {
            userService.getById(userId)
                .orElseThrow { ResourceNotFound() }
        }

        return if (userId == null) {
            recipeService.getRecent(min(limit, maxLimit))
        } else {
            recipeService.getRecentByUser(author, min(limit, maxLimit))
        }.map(mapper::toDto)
    }

    @RequiresAuthentication
    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    fun listPopularRecipes(
        @AuthorizedUser user: User,
        @RequestParam(required = false, defaultValue = "10") limit: Int,
        @RequestParam("user", required = false) userId: Long?
    ): List<RecipeDto> {
        val maxLimit = 100
        val author = if (userId == null) {
            user
        } else {
            userService.getById(userId)
                .orElseThrow { ResourceNotFound() }
        }

        return if (userId == null) {
            recipeService.getPopular(min(limit, maxLimit))
        } else {
            recipeService.getPopularByUser(author, min(limit, maxLimit))
        }.map(mapper::toDto)
    }

    @RequiresAuthentication
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    fun listRecipesByUser(
        @AuthorizedUser user: User,
        @RequestParam(required = false, defaultValue = "0") offset: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int,
        @RequestParam("user", required = false) userId: Long?
    ): List<RecipeDto> {
        val maxLimit = 100
        val author = if (userId == null) {
            user
        } else {
            userService.getById(userId)
                .orElseThrow { ResourceNotFound() }
        }

        return if (userId == null) {
            recipeService.getByAuthor(author, min(limit, maxLimit), offset)
        } else {
            recipeService.getPublicByAuthor(author, min(limit, maxLimit), offset)
        }.map(mapper::toDto)
    }

    @RequiresAuthentication
    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    fun inspectRecipeById(
        @AuthorizedUser user: User,
        @PathVariable("id") id: Long,
    ): RecipeDto {
        return recipeService.getById(id)
            .filter { user.canRead(it) }
            .map(mapper::toDto)
            .orElseThrow { ResourceNotFound() }
    }

    @RequiresAuthentication
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    fun createRecipe(
        @AuthorizedUser user: User,
        @Valid @RequestBody request: CreateRecipeRequest
    ): RecipeDto {
        val recipe = recipeService.save(mapper.toEntity(request, user))

        for (name in request.categories) {
            val category = categoryService.getByName(name)
                .orElseThrow { ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown category $name") }
            recipe.categories.add(category)
        }

        for (name in request.tags) {
            val tag = tagService.save(Tag(name = name, recipe = recipe))
            recipe.tags.add(tag)
        }

        for (name in request.ingredients) {
            val ingredient = ingredientService.getByName(name)
                .orElseGet { Ingredient(name = name) }
            recipe.ingredients.add(ingredient)
        }

        recipeService.save(recipe)

        return mapper.toDto(recipe)
    }

    @RequiresAuthentication
    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    fun replaceRecipe(
        @AuthorizedUser user: User,
        @PathVariable("id") id: Long,
        @Valid @RequestBody request: CreateRecipeRequest
    ): RecipeDto {
        val oldRecipe = recipeService.getById(id)
            .filter { user.canWrite(it) }
            .orElseThrow { ResourceNotFound() }

        val newRecipe = mapper.toEntity(request, oldRecipe.author)
        newRecipe.id = id
        newRecipe.categories.clear()
        newRecipe.tags.clear()

        for (name in request.categories) {
            val category = categoryService.getByName(name)
                .orElseThrow { ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown category $name") }
            newRecipe.categories.add(category)
        }

        for (name in request.tags) {
            val tag = tagService.save(Tag(name = name, recipe = newRecipe))
            newRecipe.tags.add(tag)
        }

        for (name in request.ingredients) {
            val ingredient = ingredientService.getByName(name)
                .orElseGet { Ingredient(name = name) }
            newRecipe.ingredients.add(ingredient)
        }

        recipeService.save(newRecipe)

        return mapper.toDto(newRecipe)
    }

    @RequiresAuthentication
    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    fun updateRecipe(
        @AuthorizedUser user: User,
        @PathVariable("id") id: Long,
        @Valid @RequestBody request: UpdateRecipeRequest
    ): RecipeDto {
        val recipe = recipeService.getById(id)
            .filter { user.canWrite(it) }
            .orElseThrow { ResourceNotFound() }

        if (request.title != null) {
            recipe.title = request.title!!
        }

        if (request.description != null) {
            recipe.description = request.description!!
        }

        if (request.prepTime != null) {
            recipe.prepTime = request.prepTime!!
        }

        if (request.cookTime != null) {
            recipe.cookTime = request.cookTime!!
        }

        if (request.yield != null) {
            recipe.yield = request.yield!!
        }

        if (request.public != null) {
            recipe.public = request.public!!
        }

        if (request.directions.isNotEmpty()) {
            recipe.directions = mapper.serializeDirections(request.directions)
        }

        // TODO tags, categories, ingredients

        recipeService.save(recipe)

        return mapper.toDto(recipe)
    }

    @RequiresAuthentication
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    fun deleteRecipeById(
        @AuthorizedUser user: User,
        @PathVariable("id") id: Long
    ) {
        recipeService.getById(id)
            .filter { user.canWrite(it) }
            .ifPresent(recipeService::delete)
    }
}
