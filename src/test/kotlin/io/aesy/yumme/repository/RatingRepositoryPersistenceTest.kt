package io.aesy.yumme.repository

import io.aesy.test.TestType
import io.aesy.yumme.entity.*
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
        val user = User(email = "test@test.com", password = "secret")
        userRepository.save(user)
        val recipe = Recipe(title = "woop", author = user, description = "woop")
        recipeRepository.save(recipe)
        val rating = Rating(score = 5, recipe = recipe)
        ratingRepository.save(rating)

        expectThat(recipe.id).isNotNull()
    }

    @Test
    fun `It should be possible to get the average score of a recipe`() {
        val user = User(email = "test@test.com", password = "secret")
        userRepository.save(user)
        val recipe = Recipe(title = "woop", author = user, description = "woop")
        recipeRepository.save(recipe)
        val rating1 = Rating(score = 5, recipe = recipe)
        ratingRepository.save(rating1)
        val rating2 = Rating(score = 0, recipe = recipe)
        ratingRepository.save(rating2)

        val average = ratingRepository.getAverageByRecipe(recipe)

        expectThat(average).isEqualTo(2.5)
    }

    @Test
    fun `It should be possible to get the average score of a recipe with no ratings`() {
        val user = User(email = "test@test.com", password = "secret")
        userRepository.save(user)
        val recipe = Recipe(title = "woop", author = user, description = "woop")
        recipeRepository.save(recipe)

        val average = ratingRepository.getAverageByRecipe(recipe)

        expectThat(average).isEqualTo(0.0)
    }

    @Test
    fun `It should be possible to get the rating count of a recipe`() {
        val user = User(email = "test@test.com", password = "secret")
        userRepository.save(user)
        val recipe = Recipe(title = "woop", author = user, description = "woop")
        recipeRepository.save(recipe)
        val rating1 = Rating(score = 5, recipe = recipe)
        ratingRepository.save(rating1)
        val rating2 = Rating(score = 0, recipe = recipe)
        ratingRepository.save(rating2)

        val count = ratingRepository.getCountByRecipe(recipe)

        expectThat(count).isEqualTo(2)
    }

    @Test
    fun `It should be possible to get the rating count of a recipe with no ratings`() {
        val user = User(email = "test@test.com", password = "secret")
        userRepository.save(user)
        val recipe = Recipe(title = "woop", author = user, description = "woop")
        recipeRepository.save(recipe)

        val count = ratingRepository.getCountByRecipe(recipe)

        expectThat(count).isEqualTo(0)
    }
}
