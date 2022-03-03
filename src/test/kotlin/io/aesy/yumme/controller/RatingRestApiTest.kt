package io.aesy.yumme.controller

import io.aesy.test.TestType
import io.aesy.test.util.Recipes
import io.aesy.test.util.Users.createUser
import io.aesy.yumme.dto.RatingSummaryDto
import io.aesy.yumme.dto.RecipeDto
import io.aesy.yumme.service.*
import io.aesy.yumme.util.Doubles.roundToDouble
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.*
import org.springframework.http.HttpStatus
import strikt.api.expectThat
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo
import strikt.java.isPresent

@TestType.RestApi
class RatingRestApiTest {
    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var recipeService: RecipeService

    @Autowired
    private lateinit var ratingService: RatingService

    @Autowired
    private lateinit var userService: UserService

    @Test
    fun `It should be possible to get rating summary of a recipe`() {
        val ratings = arrayOf(1, 3, 3, 5, 5)
        val author = userService.createUser("test@test.com", "woop", "secret")
        val recipe = recipeService.save(Recipes.random(author))

        for ((index, rating) in ratings.withIndex()) {
            val user = userService.createUser("test$index", "woop", "secret")

            ratingService.rateAsUser(user, recipe, rating)
        }

        val response = restTemplate.withBasicAuth(author.userName, "secret")
            .getForEntity<RatingSummaryDto>("/recipe/${recipe.id}/rating")

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val summary = response.body

        expectThat(summary!!.average).isEqualTo(ratings.average().roundToDouble(2))
        expectThat(summary.count).isEqualTo(ratings.size.toLong())
    }

    @Test
    fun `It should be possible to rate a recipe`() {
        val author = userService.createUser("test@test.com", "woop", "secret")
        val recipe = recipeService.save(Recipes.random(author))

        val response = restTemplate.withBasicAuth(author.userName, "secret")
            .postForEntity<RecipeDto>("/recipe/${recipe.id}/rating?score=5")

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val rating = ratingService.getByRecipeAndUser(recipe, author)

        expectThat(rating).isPresent()
    }

    @Test
    fun `Multiple ratings of the same recipe should overwrite previous ratings`() {
        val author = userService.createUser("test@test.com", "woop", "secret")
        val recipe = recipeService.save(Recipes.random(author))

        val response1 = restTemplate.withBasicAuth(author.userName, "secret")
            .postForEntity<RecipeDto>("/recipe/${recipe.id}/rating?score=3")

        expectThat(response1.statusCode).isEqualTo(HttpStatus.OK)

        val response2 = restTemplate.withBasicAuth(author.userName, "secret")
            .postForEntity<RecipeDto>("/recipe/${recipe.id}/rating?score=2")

        expectThat(response2.statusCode).isEqualTo(HttpStatus.OK)

        val ratings = ratingService.getAllByRecipe(recipe)

        expectThat(ratings).hasSize(1)
        expectThat(ratings[0].score).isEqualTo(2)
    }

    @ParameterizedTest
    @ValueSource(ints = [-1, 0, 6, 100])
    fun `It should not be possible to rate a recipe with an invalid score`(score: Int) {
        val author = userService.createUser("test@test.com", "woop", "secret")
        val recipe = recipeService.save(Recipes.random(author))

        val response = restTemplate.withBasicAuth(author.userName, "secret")
            .postForEntity<Unit>("/recipe/${recipe.id}/rating?score=$score")

        expectThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }
}
