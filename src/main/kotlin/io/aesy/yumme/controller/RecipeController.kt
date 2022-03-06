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
import java.time.Duration
import java.time.Instant
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
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    fun searchRecipes(
        @RequestParam(required = false, defaultValue = "0") offset: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int,
        @RequestParam("author", required = false) authorId: Long?,
        @RequestParam("q", required = false) q: String?,
        @RequestParam("min-prep-time", required = false) minPrepTime: Duration?,
        @RequestParam("max-prep-time", required = false) maxPrepTime: Duration?,
        @RequestParam("min-cook-time", required = false) minCookTime: Duration?,
        @RequestParam("max-cook-time", required = false) maxCookTime: Duration?,
        @RequestParam("created-before", required = false) createdBefore: Instant?,
        @RequestParam("created-after", required = false) createdAfter: Instant?,
        @RequestParam("include-ingredient", required = false) includeIngredients: List<Long>?,
        @RequestParam("exclude-ingredient", required = false) excludeIngredients: List<Long>?,
        @RequestParam("include-tag", required = false) includeTag: List<Long>?,
        @RequestParam("exclude-tag", required = false) excludeTag: List<Long>?,
        @RequestParam("include-category", required = false) includeCategories: List<Long>?,
        @RequestParam("exclude-category", required = false) excludeCategories: List<Long>?
    ): List<RecipeDto> {
        val maxLimit = 100
        val query = recipeService.query()

        authorId?.apply(query::byAuthor)
        q?.apply(query::freetext)
        minPrepTime?.apply(query::withMinPrepTime)
        maxPrepTime?.apply(query::withMaxPrepTime)
        minCookTime?.apply(query::withMinCookTime)
        maxCookTime?.apply(query::withMaxCookTime)
        createdBefore?.apply(query::isCreatedBefore)
        createdAfter?.apply(query::isCreatedAfter)
        includeIngredients?.apply(query::includingIngredients)
        excludeIngredients?.apply(query::notIncludingIngredients)
        includeTag?.apply(query::withTags)
        excludeTag?.apply(query::withoutTags)
        includeCategories?.apply(query::inCategories)
        excludeCategories?.apply(query::notInCategories)

        return query
            .public(true)
            .search(min(limit, maxLimit), offset)
            .map(mapper::toDto)
    }

    @RequiresAuthentication
    @GetMapping("/recent")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    fun listRecentRecipes(
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
            recipeService.getRecent(min(limit, maxLimit), offset)
        } else {
            recipeService.getRecentByUser(author, min(limit, maxLimit), offset)
        }.map(mapper::toDto)
    }

    @RequiresAuthentication
    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    fun listPopularRecipes(
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
            recipeService.getPopular(min(limit, maxLimit), offset)
        } else {
            recipeService.getPopularByUser(author, min(limit, maxLimit), offset)
        }.map(mapper::toDto)
    }

    @RequiresAuthentication
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    fun listRecipes(
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
