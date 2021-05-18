package io.aesy.yumme.mapper

import io.aesy.yumme.dto.*
import io.aesy.yumme.entity.*
import io.aesy.yumme.entity.RecipeHasImageUpload.Type
import io.aesy.yumme.repository.RecipeHasImageUploadRepository
import io.aesy.yumme.service.RatingService
import org.springframework.stereotype.Service
import org.springframework.web.util.HtmlUtils

@Service
class RecipeMapper(
    private val ratingService: RatingService,
    private val recipeHasImageUploadRepository: RecipeHasImageUploadRepository
) {
    fun toEntity(request: CreateRecipeRequest, author: User): Recipe = Recipe(
        title = request.title!!,
        description = request.description!!,
        directions = serializeDirections(request.directions),
        prepTime = request.prepTime!!,
        cookTime = request.cookTime!!,
        yield = request.yield!!,
        author = author
    ).apply {
        public = request.public!!
    }

    fun toDto(recipe: Recipe): RecipeDto = RecipeDto(
        id = recipe.id,
        title = recipe.title,
        description = recipe.description,
        directions = deserializeDirections(recipe.directions),
        prepTime = recipe.prepTime,
        cookTime = recipe.cookTime,
        yield = recipe.yield,
        rating = ratingService.getRatingSummary(recipe),
        tags = recipe.tags
            .map(Tag::name)
            .toMutableSet(),
        categories = recipe.categories
            .map(Category::name)
            .toMutableSet(),
        ingredients = recipe.ingredients
            .map { IngredientDto(it.name) }
            .toMutableSet(),
        images = recipeHasImageUploadRepository.findByRecipeAndType(recipe, Type.ORIGINAL)
            .map(RecipeHasImageUpload::name)
            .map { "$it.png" }
            .toMutableSet()
    )

    fun serializeDirections(directions: List<String>): String = directions
        .joinToString(
            "</section><section>",
            "<section>",
            "</section>",
            transform = HtmlUtils::htmlEscape
        )

    fun deserializeDirections(directions: String): MutableList<String> = directions
        .removePrefix("<section>")
        .removeSuffix("</section>")
        .split("</section><section>")
        .map(HtmlUtils::htmlUnescape)
        .toMutableList()
}