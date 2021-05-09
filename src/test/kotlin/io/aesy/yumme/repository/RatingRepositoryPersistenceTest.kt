package io.aesy.yumme.repository

import io.aesy.test.TestType
import io.aesy.yumme.entity.Rating
import io.aesy.yumme.util.Recipes
import io.aesy.yumme.util.Users
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import strikt.api.expectThat
import strikt.assertions.*

@TestType.Persistence
class RatingRepositoryPersistenceTest {
    @Autowired
    private lateinit var ratingRepository: RatingRepository

    @Autowired
    private lateinit var recipeRepository: RecipeRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `It should be possible to persist a rating`() {
        val author = userRepository.save(Users.random())
        val recipe = recipeRepository.save(Recipes.random(author))
        val rating = Rating(score = 5, recipe = recipe, user = author)

        ratingRepository.save(rating)

        expectThat(recipe.id).isNotNull()
    }

    @Test
    fun `It should be possible to get ratings by recipe`() {
        val author1 = userRepository.save(Users.random())
        val author2 = userRepository.save(Users.random())
        val recipe = recipeRepository.save(Recipes.random(author1))
        ratingRepository.save(Rating(score = 1, recipe = recipe, user = author1))
        ratingRepository.save(Rating(score = 5, recipe = recipe, user = author2))

        val recipes = ratingRepository.findAllByRecipe(recipe)

        expectThat(recipes).hasSize(2)
    }

    @Test
    fun `It should be possible to get ratings by recipe and user`() {
        val author1 = userRepository.save(Users.random())
        val author2 = userRepository.save(Users.random())
        val recipe = recipeRepository.save(Recipes.random(author1))
        ratingRepository.save(Rating(score = 1, recipe = recipe, user = author1))
        ratingRepository.save(Rating(score = 5, recipe = recipe, user = author2))

        val ratings = ratingRepository.findAllByRecipe(recipe)

        expectThat(ratings).hasSize(2)
    }

    @Test
    fun `It should be possible to get the average score of a recipe`() {
        val author1 = userRepository.save(Users.random())
        val author2 = userRepository.save(Users.random())
        val recipe = recipeRepository.save(Recipes.random(author1))
        ratingRepository.save(Rating(score = 5, recipe = recipe, user = author1))
        ratingRepository.save(Rating(score = 0, recipe = recipe, user = author2))

        val average = ratingRepository.findAverageByRecipe(recipe)

        expectThat(average).isEqualTo(2.5)
    }

    @Test
    fun `It should be possible to get the average score of a recipe with no ratings`() {
        val author = userRepository.save(Users.random())
        val recipe = recipeRepository.save(Recipes.random(author))

        val average = ratingRepository.findAverageByRecipe(recipe)

        expectThat(average).isEqualTo(0.0)
    }

    @Test
    fun `It should be possible to get the rating count of a recipe`() {
        val author1 = userRepository.save(Users.random())
        val author2 = userRepository.save(Users.random())
        val recipe = recipeRepository.save(Recipes.random(author1))
        ratingRepository.save(Rating(score = 5, recipe = recipe, user = author1))
        ratingRepository.save(Rating(score = 0, recipe = recipe, user = author2))

        val count = ratingRepository.findCountByRecipe(recipe)

        expectThat(count).isEqualTo(2)
    }

    @Test
    fun `It should be possible to get the rating count of a recipe with no ratings`() {
        val author = userRepository.save(Users.random())
        val recipe = recipeRepository.save(Recipes.random(author))

        val count = ratingRepository.findCountByRecipe(recipe)

        expectThat(count).isEqualTo(0)
    }
}
