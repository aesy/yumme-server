package io.aesy.yumme.controller

import io.aesy.yumme.auth.AuthorizedUser
import io.aesy.yumme.dto.TagDto
import io.aesy.yumme.entity.Role
import io.aesy.yumme.entity.User
import io.aesy.yumme.exception.ResourceNotFound
import io.aesy.yumme.mapper.TagMapper
import io.aesy.yumme.service.RecipeService
import io.aesy.yumme.service.TagService
import io.aesy.yumme.util.AccessControl.canRead
import io.swagger.v3.oas.annotations.tags.Tag
import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.apache.shiro.authz.annotation.RequiresRoles
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional
import kotlin.math.min

@Tag(name = "Tag")
@RestController
@RequestMapping(
    consumes = [MediaType.APPLICATION_JSON_VALUE, MediaType.ALL_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
@Validated
class TagController(
    private val recipeService: RecipeService,
    private val tagService: TagService,
    private val mapper: TagMapper
) {
    @RequiresAuthentication
    @RequiresRoles(Role.ADMIN)
    @GetMapping("/tag")
    @Transactional
    fun listTags(
        @RequestParam(required = false, defaultValue = "0") offset: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): List<TagDto> {
        val maxLimit = 100

        return tagService.getAll(min(limit, maxLimit), offset)
            .map(mapper::toDto)
    }

    @RequiresAuthentication
    @GetMapping("/recipe/{id}/tag")
    @Transactional
    @Tag(name = "Recipe")
    fun listTagsByRecipe(
        @AuthorizedUser user: User,
        @PathVariable("id") id: Long,
        @RequestParam(required = false, defaultValue = "0") offset: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): List<TagDto> {
        val maxLimit = 100

        val recipe = recipeService.getById(id)
            .filter { user.canRead(it) }
            .orElseThrow { ResourceNotFound.recipe(id) }

        return tagService.getAllByRecipe(recipe, min(limit, maxLimit), offset)
            .map(mapper::toDto)
    }
}
