package io.aesy.yumme.repository

import io.aesy.test.TestType
import io.aesy.test.util.Recipes
import io.aesy.test.util.Users
import io.aesy.yumme.entity.*
import io.aesy.yumme.repository.matcher.RecipeMatcher
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import strikt.api.expectThat
import strikt.assertions.*
import java.time.Duration
import java.time.Instant

@TestType.Persistence
class RecipeRepositoryPersistenceTest {
    @Autowired
    private lateinit var recipeRepository: RecipeRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var ingredientRepository: IngredientRepository

    @Autowired
    private lateinit var tagRepository: TagRepository

    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    @Test
    fun `It should be possible to persist a recipe`() {
        val author = userRepository.save(Users.random())
        val recipe = recipeRepository.save(Recipes.random(author))

        expectThat(recipe.id).isNotNull()
    }

    @Test
    fun `It should be possible to fetch recipes by author`() {
        val author1 = userRepository.save(Users.random())
        val author2 = userRepository.save(Users.random())
        val recipe1 = recipeRepository.save(Recipes.random(author1))
        recipeRepository.save(Recipes.random(author2))

        val recipes = recipeRepository.findAllByAuthor(author1)

        expectThat(recipes)
            .map(Recipe::id)
            .containsExactly(recipe1.id)
    }

    @Test
    fun `It should be possible to fetch all public recipes`() {
        val author = userRepository.save(Users.random())
        val recipe1 = Recipes.random(author)
        recipe1.public = true
        recipeRepository.save(recipe1)
        val recipe2 = Recipes.random(author)
        recipe2.public = false
        recipeRepository.save(recipe2)

        val recipes = recipeRepository.findAllPublic()

        expectThat(recipes).hasSize(1)
    }

    @Test
    fun `It should be possible to fetch all public recipes in pages`() {
        val author1 = userRepository.save(Users.random())
        val author2 = userRepository.save(Users.random())
        val recipe1 = Recipes.random(author1)
        recipe1.public = true
        recipeRepository.save(recipe1)
        val recipe2 = Recipes.random(author1)
        recipe2.public = false
        recipeRepository.save(recipe2)
        val recipe3 = Recipes.random(author2)
        recipe3.public = true
        recipeRepository.save(recipe3)

        val result = recipeRepository.findAllPublic(PageRequest.of(0, 3))

        expectThat(result)
            .map(Recipe::id)
            .containsExactlyInAnyOrder(recipe1.id, recipe3.id)
    }

    @Test
    fun `It should be possible to search recipes by title`() {
        val author = userRepository.save(Users.random())
        val recipe1 = Recipes.random(author)
        recipe1.title = "woop"
        recipeRepository.save(recipe1)
        val recipe2 = Recipes.random(author)
        recipe2.title = "oo"
        recipeRepository.save(recipe2)
        val recipe3 = Recipes.random(author)
        recipe3.title = "wawawa"
        recipeRepository.save(recipe3)

        val resultSuccess = recipeRepository.findAll(RecipeMatcher.hasTitleLike("oo"))
        val resultFailure = recipeRepository.findAll(RecipeMatcher.hasTitleLike("non-match"))

        expectThat(resultSuccess).hasSize(2)
        expectThat(resultFailure).isEmpty()
    }

    @Test
    fun `It should be possible to search recipes by description`() {
        val author = userRepository.save(Users.random())
        val recipe1 = Recipes.random(author)
        recipe1.description = "woop"
        recipeRepository.save(recipe1)
        val recipe2 = Recipes.random(author)
        recipe2.description = "oo"
        recipeRepository.save(recipe2)
        val recipe3 = Recipes.random(author)
        recipe3.description = "wawawa"
        recipeRepository.save(recipe3)

        val resultSuccess = recipeRepository.findAll(RecipeMatcher.hasDescriptionLike("oo"))
        val resultFailure = recipeRepository.findAll(RecipeMatcher.hasDescriptionLike("non-match"))

        expectThat(resultSuccess).hasSize(2)
        expectThat(resultFailure).isEmpty()
    }

    @Test
    fun `It should be possible to search recipes by directions`() {
        val author = userRepository.save(Users.random())
        val recipe1 = Recipes.random(author)
        recipe1.directions = "woop"
        recipeRepository.save(recipe1)
        val recipe2 = Recipes.random(author)
        recipe2.directions = "oo"
        recipeRepository.save(recipe2)
        val recipe3 = Recipes.random(author)
        recipe3.directions = "wawawa"
        recipeRepository.save(recipe3)

        val resultSuccess = recipeRepository.findAll(RecipeMatcher.hasDirectionsLike("oo"))
        val resultFailure = recipeRepository.findAll(RecipeMatcher.hasDirectionsLike("non-match"))

        expectThat(resultSuccess).hasSize(2)
        expectThat(resultFailure).isEmpty()
    }

    @Test
    fun `It should be possible to search recipes by minimum prep time`() {
        val author = userRepository.save(Users.random())
        val recipe1 = Recipes.random(author)
        recipe1.prepTime = Duration.ofDays(1)
        recipeRepository.save(recipe1)
        val recipe2 = Recipes.random(author)
        recipe2.prepTime = Duration.ofDays(3)
        recipeRepository.save(recipe2)

        val resultSuccess = recipeRepository.findAll(RecipeMatcher.hasMinPrepTime(Duration.ofDays(2)))
        val resultFailure = recipeRepository.findAll(RecipeMatcher.hasMinPrepTime(Duration.ofDays(4)))

        expectThat(resultSuccess).hasSize(1)
        expectThat(resultFailure).isEmpty()
    }

    @Test
    fun `It should be possible to search recipes by maximum prep time`() {
        val author = userRepository.save(Users.random())
        val recipe1 = Recipes.random(author)
        recipe1.prepTime = Duration.ofDays(2)
        recipeRepository.save(recipe1)
        val recipe2 = Recipes.random(author)
        recipe2.prepTime = Duration.ofDays(4)
        recipeRepository.save(recipe2)

        val resultSuccess = recipeRepository.findAll(RecipeMatcher.hasMaxPrepTime(Duration.ofDays(3)))
        val resultFailure = recipeRepository.findAll(RecipeMatcher.hasMaxPrepTime(Duration.ofDays(1)))

        expectThat(resultSuccess).hasSize(1)
        expectThat(resultFailure).isEmpty()
    }

    @Test
    fun `It should be possible to search recipes by minimum cook time`() {
        val author = userRepository.save(Users.random())
        val recipe1 = Recipes.random(author)
        recipe1.cookTime = Duration.ofDays(1)
        recipeRepository.save(recipe1)
        val recipe2 = Recipes.random(author)
        recipe2.cookTime = Duration.ofDays(3)
        recipeRepository.save(recipe2)

        val resultSuccess = recipeRepository.findAll(RecipeMatcher.hasMinCookTime(Duration.ofDays(2)))
        val resultFailure = recipeRepository.findAll(RecipeMatcher.hasMinCookTime(Duration.ofDays(4)))

        expectThat(resultSuccess).hasSize(1)
        expectThat(resultFailure).isEmpty()
    }

    @Test
    fun `It should be possible to search recipes by maximum cook time`() {
        val author = userRepository.save(Users.random())
        val recipe1 = Recipes.random(author)
        recipe1.cookTime = Duration.ofDays(2)
        recipeRepository.save(recipe1)
        val recipe2 = Recipes.random(author)
        recipe2.cookTime = Duration.ofDays(4)
        recipeRepository.save(recipe2)

        val resultSuccess = recipeRepository.findAll(RecipeMatcher.hasMaxCookTime(Duration.ofDays(3)))
        val resultFailure = recipeRepository.findAll(RecipeMatcher.hasMaxCookTime(Duration.ofDays(1)))

        expectThat(resultSuccess).hasSize(1)
        expectThat(resultFailure).isEmpty()
    }

    @Test
    fun `It should be possible to search recipes by author`() {
        val author1 = userRepository.save(Users.random())
        val author2 = userRepository.save(Users.random())
        recipeRepository.save(Recipes.random(author1))

        val resultSuccess = recipeRepository.findAll(RecipeMatcher.byAuthor(author1.id!!))
        val resultFailure = recipeRepository.findAll(RecipeMatcher.byAuthor(author2.id!!))

        expectThat(resultSuccess).isNotEmpty()
        expectThat(resultFailure).isEmpty()
    }

    @Test
    fun `It should be possible to search recipes by public flag`() {
        val author = userRepository.save(Users.random())
        val recipe = Recipes.random(author)
        recipe.public = true
        recipeRepository.save(recipe)

        val resultSuccess = recipeRepository.findAll(RecipeMatcher.isPublic(true))
        val resultFailure = recipeRepository.findAll(RecipeMatcher.isPublic(false))

        expectThat(resultSuccess).isNotEmpty()
        expectThat(resultFailure).isEmpty()
    }

    @Test
    fun `It should be possible to search recipes by minimum creation date`() {
        val now = Instant.now()
        val author = userRepository.save(Users.random())
        val recipe = Recipes.random(author)
        recipe.createdAt = now
        recipeRepository.save(recipe)

        val resultSuccess = recipeRepository.findAll(RecipeMatcher.isCreatedAfter(now.minusSeconds(10)))
        val resultFailure = recipeRepository.findAll(RecipeMatcher.isCreatedAfter(now.plusSeconds(10)))

        expectThat(resultSuccess).isNotEmpty()
        expectThat(resultFailure).isEmpty()
    }

    @Test
    fun `It should be possible to search recipes by maximum creation date`() {
        val now = Instant.now()
        val author = userRepository.save(Users.random())
        val recipe = Recipes.random(author)
        recipe.createdAt = now
        recipeRepository.save(recipe)

        val resultSuccess = recipeRepository.findAll(RecipeMatcher.isCreatedBefore(now.plusSeconds(10)))
        val resultFailure = recipeRepository.findAll(RecipeMatcher.isCreatedBefore(now.minusSeconds(10)))

        expectThat(resultSuccess).isNotEmpty()
        expectThat(resultFailure).isEmpty()
    }

    @Test
    fun `It should be possible to search recipes by recipe`() {
        val author = userRepository.save(Users.random())
        val ingredient1 = ingredientRepository.save(Ingredient(name = "woop"))
        val ingredient2 = ingredientRepository.save(Ingredient(name = "wawawa"))
        val recipe = recipeRepository.save(Recipes.random(author))
        recipe.ingredients.add(ingredient1)
        recipeRepository.save(recipe)
        ingredient1.recipes

        val resultSuccess = recipeRepository.findAll(RecipeMatcher.hasIngredient(ingredient1.id!!))
        val resultFailure = recipeRepository.findAll(RecipeMatcher.hasIngredient(ingredient2.id!!))

        expectThat(resultSuccess).isNotEmpty()
        expectThat(resultFailure).isEmpty()
    }

    @Test
    fun `It should be possible to search recipes by tag`() {
        val author = userRepository.save(Users.random())
        val recipe1 = recipeRepository.save(Recipes.random(author))
        val recipe2 = recipeRepository.save(Recipes.random(author))
        val tag1 = tagRepository.save(Tag(name = "woop", recipe = recipe1))
        val tag2 = tagRepository.save(Tag(name = "wawawa", recipe = recipe2))

        val result1 = recipeRepository.findAll(RecipeMatcher.hasTag(tag1.id!!))
        val result2 = recipeRepository.findAll(RecipeMatcher.hasTag(tag2.id!!))

        expectThat(result1).map(Recipe::id).containsExactly(recipe1.id)
        expectThat(result2).map(Recipe::id).containsExactly(recipe2.id)
    }

    @Test
    fun `It should be possible to search recipes by category`() {
        val author = userRepository.save(Users.random())
        val category1 = categoryRepository.save(Category(name = "woop"))
        val category2 = categoryRepository.save(Category(name = "wawawa"))
        val recipe = recipeRepository.save(Recipes.random(author))
        recipe.categories.add(category1)
        recipeRepository.save(recipe)

        val resultSuccess = recipeRepository.findAll(RecipeMatcher.hasCategory(category1.id!!))
        val resultFailure = recipeRepository.findAll(RecipeMatcher.hasCategory(category2.id!!))

        expectThat(resultSuccess).isNotEmpty()
        expectThat(resultFailure).isEmpty()
    }
}
