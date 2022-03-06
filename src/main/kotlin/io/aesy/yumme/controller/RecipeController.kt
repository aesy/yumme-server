package io.aesy.yumme.controller

import io.aesy.yumme.auth.AuthorizedUser
import io.aesy.yumme.dto.*
import io.aesy.yumme.entity.User
import io.aesy.yumme.exception.ResourceNotFound
import io.aesy.yumme.mapper.RecipeMapper
import io.aesy.yumme.service.RecipeService
import io.aesy.yumme.service.UserService
import io.aesy.yumme.util.AccessControl.canRead
import io.aesy.yumme.util.AccessControl.canWrite
import io.swagger.v3.oas.annotations.tags.Tag
import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional
import javax.validation.Valid
import kotlin.math.min

@Tag(name = "Recipe")
@RestController
@RequestMapping(
    "recipe",
    consumes = [MediaType.APPLICATION_JSON_VALUE, MediaType.ALL_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
@Validated
class RecipeController(
    private val recipeService: RecipeService,
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
                .orElseThrow { ResourceNotFound.user(userId) }
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
                .orElseThrow { ResourceNotFound.user(userId) }
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
                .orElseThrow { ResourceNotFound.user(userId) }
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
            .orElseThrow { ResourceNotFound.recipe(id) }
    }

    @RequiresAuthentication
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    fun createRecipe(
        @AuthorizedUser user: User,
        @Valid @RequestBody request: CreateRecipeRequest
    ): RecipeDto {
        val recipe = mapper.toEntity(request, user)

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
        val recipe = recipeService.getById(id)
            .filter { user.canWrite(it) }
            .map { mapper.toEntity(request, it) }
            .orElseThrow { ResourceNotFound.recipe(id) }

        recipeService.save(recipe)

        return mapper.toDto(recipe)
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
            .map { mapper.toEntity(request, it) }
            .orElseThrow { ResourceNotFound.recipe(id) }

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
