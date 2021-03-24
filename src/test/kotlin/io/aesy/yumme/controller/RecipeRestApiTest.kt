package io.aesy.yumme.controller

import io.aesy.test.TestType
import io.aesy.yumme.dto.RecipeDto
import io.aesy.yumme.entity.Recipe
import io.aesy.yumme.entity.User
import io.aesy.yumme.request.CreateRecipeRequest
import io.aesy.yumme.service.RecipeService
import io.aesy.yumme.service.UserService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.*
import org.springframework.http.HttpStatus
import strikt.api.expectThat
import strikt.assertions.*
import strikt.java.isAbsent
import java.time.Duration

@TestType.RestApi
class RecipeRestApiTest {
    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var recipeService: RecipeService

    @Autowired
    private lateinit var userService: UserService

    @Test
    fun `It should be possible to fetch the most recently created public recipes`() {
        val user1 = userService.save(User(email = "test1@test.com", password = "secret"))
        val user2 = userService.save(User(email = "test2@test.com", password = "secret"))
        val recipe1 = Recipe(title = "woop", description = "woop", author = user1)
        recipe1.public = true
        recipeService.save(recipe1)
        val recipe2 = Recipe(title = "woop", description = "woop", author = user1)
        recipe2.public = false
        recipeService.save(recipe2)

        val response = restTemplate.withBasicAuth(user2.email, user2.password)
            .getForEntity<List<RecipeDto>>("/recipe/recent")

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isNotNull().hasSize(1)
        // TODO verify creation time
    }

    @Test
    fun `It should be possible to fetch the most popular public recipes`() {
        val user1 = userService.save(User(email = "test1@test.com", password = "secret"))
        val user2 = userService.save(User(email = "test2@test.com", password = "secret"))
        val recipe1 = Recipe(title = "woop", description = "woop", author = user1)
        recipe1.public = true
        recipeService.save(recipe1)
        val recipe2 = Recipe(title = "woop", description = "woop", author = user1)
        recipe2.public = false
        recipeService.save(recipe2)

        val response = restTemplate.withBasicAuth(user2.email, user2.password)
            .getForEntity<List<RecipeDto>>("/recipe/popular")

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isNotNull().hasSize(1)
        // TODO verify rating
    }

    @Test
    fun `It should be possible to create a new recipe`() {
        val user = userService.save(User(email = "test2@test.com", password = "secret"))
        val request = CreateRecipeRequest("woop", "woop", true, Duration.ofHours(1))

        val response = restTemplate.withBasicAuth(user.email, user.password)
            .postForEntity<RecipeDto>("/recipe", request)

        expectThat(response.statusCode).isEqualTo(HttpStatus.CREATED)

        val recipe = response.body

        expectThat(recipe).isNotNull()
        expectThat(recipe!!.id).isNotNull()
    }

    @Test
    fun `It should be possible to fetch a recipe by id`() {
        val user = userService.save(User(email = "test2@test.com", password = "secret"))
        val recipe = Recipe(title = "woop", description = "woop", author = user)
        recipeService.save(recipe)

        val response = restTemplate.withBasicAuth(user.email, user.password)
            .getForEntity<RecipeDto>("/recipe/${recipe.id}")

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)

        expectThat(response.body).isNotNull()
        expectThat(response.body!!.id).isEqualTo(recipe.id)
    }

    @Test
    fun `It should be possible to delete a recipe by id`() {
        val user = userService.save(User(email = "test2@test.com", password = "secret"))
        val recipe = Recipe(title = "woop", description = "woop", author = user)
        recipeService.save(recipe)

        restTemplate.withBasicAuth(user.email, user.password)
            .delete("/recipe/${recipe.id}")

        expectThat(recipeService.getById(recipe.id!!)).isAbsent()
    }
}
