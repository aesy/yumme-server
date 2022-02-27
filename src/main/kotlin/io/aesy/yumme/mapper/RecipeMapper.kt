package io.aesy.yumme.mapper

import io.aesy.yumme.dto.*
import io.aesy.yumme.entity.*
import io.aesy.yumme.entity.RecipeHasImageUpload.Type
import io.aesy.yumme.repository.RecipeHasImageUploadRepository
import io.aesy.yumme.service.*
import org.springframework.stereotype.Service
import org.springframework.web.util.HtmlUtils

@Service
class RecipeMapper(
    private val ratingService: RatingService,
    private val categoryService: CategoryService,
    private val ingredientService: IngredientService,
    private val recipeHasImageUploadRepository: RecipeHasImageUploadRepository
) {
    fun toEntity(request: CreateRecipeRequest, author: User): Recipe = Recipe(
        title = request.title,
        description = request.description,
        directions = serializeDirections(request.directions),
        prepTime = request.prepTime,
        cookTime = request.cookTime,
        yield = request.yield,
        author = author
    ).apply {
        public = request.public
        categories = request.categories
            .mapNotNull { categoryService.getByName(it).orElse(null) }
            .toMutableSet()
        tags = request.tags
            .map { Tag(name = it, recipe = this) }
            .toMutableSet()
        ingredients = request.ingredients
            .map { ingredientService.getByName(it).orElseGet { Ingredient(name = it) } }
            .toMutableSet()
    }

    fun toEntity(request: CreateRecipeRequest, recipe: Recipe): Recipe = Recipe(
        id = recipe.id,
        title = request.title,
        description = request.description,
        directions = serializeDirections(request.directions),
        prepTime = request.prepTime,
        cookTime = request.cookTime,
        yield = request.yield,
        author = recipe.author
    ).apply {
        public = request.public
        categories = request.categories
            .mapNotNull { name ->
                recipe.categories.find { it.name == name }
                    ?: categoryService.getByName(name).orElse(null)
            }
            .toMutableSet()
        tags = request.tags
            .map { name ->
                recipe.tags.find { it.name == name }
                    ?: Tag(name = name, recipe = this)
            }
            .toMutableSet()
        ingredients = request.ingredients
            .map { name ->
                recipe.ingredients.find { it.name == name }
                    ?: ingredientService.getByName(name)
                        .orElseGet { Ingredient(name = name) }
            }
            .toMutableSet()
    }

    fun toEntity(request: UpdateRecipeRequest, recipe: Recipe): Recipe = Recipe(
        id = recipe.id,
        title = request.title ?: recipe.title,
        description = request.description ?: recipe.description,
        directions = request.directions
            ?.run(::serializeDirections)
            ?: recipe.directions,
        prepTime = request.prepTime ?: recipe.prepTime,
        cookTime = request.cookTime ?: recipe.cookTime,
        yield = request.yield ?: recipe.yield,
        author = recipe.author
    ).apply {
        public = request.public ?: recipe.public
        categories = request.categories
            ?.mapNotNull { name ->
                recipe.categories.find { it.name == name }
                    ?: categoryService.getByName(name).orElse(null)
            }
            ?.toMutableSet()
            ?: recipe.categories
        tags = request.tags
            ?.map { name ->
                recipe.tags.find { it.name == name }
                    ?: Tag(name = name, recipe = this)
            }
            ?.toMutableSet()
            ?: recipe.tags
        ingredients = request.ingredients
            ?.map { name ->
                recipe.ingredients.find { it.name == name }
                    ?: ingredientService.getByName(name)
                        .orElseGet { Ingredient(name = name) }
            }
            ?.toMutableSet()
            ?: recipe.ingredients
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
