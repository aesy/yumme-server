package io.aesy.yumme.controller

import io.aesy.yumme.auth.AuthorizedUser
import io.aesy.yumme.dto.CollectionDto
import io.aesy.yumme.dto.UpdateCollectionRequest
import io.aesy.yumme.entity.Collection
import io.aesy.yumme.entity.User
import io.aesy.yumme.exception.ResourceNotFound
import io.aesy.yumme.mapper.CollectionMapper
import io.aesy.yumme.service.CollectionService
import io.aesy.yumme.service.RecipeService
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

@Tag(name = "Collection")
@RestController
@RequestMapping(
    "collection",
    consumes = [MediaType.APPLICATION_JSON_VALUE, MediaType.ALL_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
@Validated
class CollectionController(
    private val collectionService: CollectionService,
    private val recipeService: RecipeService,
    private val mapper: CollectionMapper
) {
    @RequiresAuthentication
    @GetMapping
    @Transactional
    fun listCollections(
        @AuthorizedUser user: User,
        @RequestParam(required = false, defaultValue = "0") offset: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): List<CollectionDto> {
        val maxLimit = 10

        return collectionService.getByOwner(user, min(limit, maxLimit), offset)
            .map(mapper::toDto)
    }

    @RequiresAuthentication
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    fun createCollection(
        @AuthorizedUser user: User,
        @Valid @RequestBody request: UpdateCollectionRequest
    ): CollectionDto {
        val collection = collectionService.save(Collection(title = request.title!!, owner = user))

        request.recipes
            .map {
                recipeService.getById(it)
                    .orElseThrow { ResourceNotFound.recipe(it) }
            }
            .filter { user.canRead(it) }
            .apply(collection.recipes::addAll)

        collectionService.save(collection)

        return mapper.toDto(collection)
    }

    @RequiresAuthentication
    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    fun replaceCollection(
        @AuthorizedUser user: User,
        @PathVariable("id") id: Long,
        @Valid @RequestBody request: UpdateCollectionRequest
    ): CollectionDto {
        val collection = collectionService.getById(id)
            .filter { user.canWrite(it) }
            .orElseThrow { ResourceNotFound.collection(id) }

        collection.title = request.title!!
        collection.recipes.clear()

        request.recipes
            .map {
                recipeService.getById(it)
                    .orElseThrow { ResourceNotFound.recipe(it) }
            }
            .filter { user.canRead(it) }
            .apply(collection.recipes::addAll)

        collectionService.save(collection)

        return mapper.toDto(collection)
    }

    @RequiresAuthentication
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    fun deleteCollectionById(
        @AuthorizedUser user: User,
        @PathVariable("id") id: Long
    ) {
        collectionService.getById(id)
            .filter { user.canWrite(it) }
            .ifPresent(collectionService::delete)
    }
}
