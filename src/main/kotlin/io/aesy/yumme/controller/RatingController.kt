package io.aesy.yumme.controller

import io.aesy.yumme.auth.AuthorizedUser
import io.aesy.yumme.dto.RatingSummaryDto
import io.aesy.yumme.entity.User
import io.aesy.yumme.exception.ResourceNotFound
import io.aesy.yumme.service.RatingService
import io.aesy.yumme.service.RecipeService
import io.aesy.yumme.util.AccessControl.canRead
import io.aesy.yumme.util.AccessControl.isAuthor
import io.swagger.v3.oas.annotations.tags.Tag
import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.Min

@Tag(name = "Rating")
@RestController
@RequestMapping(
    "recipe/{id}/rating",
    consumes = [MediaType.APPLICATION_JSON_VALUE, MediaType.ALL_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
@Validated
class RatingController(
    private val recipeService: RecipeService,
    private val ratingService: RatingService,
) {
    @RequiresAuthentication
    @GetMapping
    @Transactional
    fun getRatingSummary(
        @AuthorizedUser user: User,
        @PathVariable("id") id: Long
    ): RatingSummaryDto {
        val recipe = recipeService.getById(id)
            .filter { user.canRead(it) }
            .orElseThrow { ResourceNotFound() }

        return ratingService.getRatingSummary(recipe)
    }

    @RequiresAuthentication
    @PostMapping
    @Transactional
    fun rateRecipe(
        @AuthorizedUser user: User,
        @PathVariable("id") id: Long,
        @Valid @RequestParam @Min(1) @Max(5) score: Int
    ) {
        val recipe = recipeService.getById(id)
            .filter { it.public or user.isAuthor(it) }
            .orElseThrow { ResourceNotFound() }

        ratingService.rateAsUser(user, recipe, score)
    }
}
