package io.aesy.yumme.controller

import io.aesy.test.TestType
import io.aesy.yumme.dto.*
import io.aesy.yumme.entity.Category
import io.aesy.yumme.service.*
import io.aesy.yumme.util.HTTP.getList
import io.aesy.yumme.util.Recipes
import io.aesy.yumme.util.Users.createUser
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.*
import org.springframework.http.HttpStatus
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.*
import strikt.java.isAbsent
import java.time.Duration
import java.time.Instant

@TestType.RestApi
class RecipeRestApiTest {
    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var recipeService: RecipeService

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var categoryService: CategoryService

    @BeforeEach
    fun setup() {
        // For PATCH support...
        restTemplate.restTemplate.requestFactory = HttpComponentsClientHttpRequestFactory()
    }

    @Test
    fun `It should be possible to list the authenticated users recipes`() {
        val author1 = userService.createUser("test1@test.com", "woop", "secret")
        val author2 = userService.createUser("test2@test.com", "woop", "secret")
        val recipe1 = Recipes.random(author1)
        recipe1.public = true
        recipeService.save(recipe1)
        val recipe2 = Recipes.random(author1)
        recipe2.public = false
        recipeService.save(recipe2)
        val recipe3 = Recipes.random(author2)
        recipe3.public = true
        recipeService.save(recipe3)

        val response = restTemplate.withBasicAuth(author1.userName, "secret")
            .getList<RecipeDto>("/recipe")

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body)
            .isNotNull()
            .map(RecipeDto::id)
            .containsExactly(recipe1.id, recipe2.id)
    }

    @Test
    fun `It should be possible to list recipes by user`() {
        val author1 = userService.createUser("test1@test.com", "woop", "secret")
        val author2 = userService.createUser("test2@test.com", "woop", "secret")
        val recipe1 = Recipes.random(author1)
        recipe1.public = true
        recipeService.save(recipe1)
        val recipe2 = Recipes.random(author2)
        recipe2.public = false
        recipeService.save(recipe2)
        val recipe3 = Recipes.random(author2)
        recipe3.public = true
        recipeService.save(recipe3)

        val response = restTemplate.withBasicAuth(author1.userName, "secret")
            .getList<RecipeDto>("/recipe?user=${author2.id}")

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body)
            .isNotNull()
            .map(RecipeDto::id)
            .containsExactly(recipe3.id)
    }

    @Test
    fun `It should be possible to fetch the most popular public recipes`() {
        val author1 = userService.createUser("test1@test.com", "woop", "secret")
        val author2 = userService.createUser("test2@test.com", "woop", "secret")
        val recipe1 = Recipes.random(author1)
        recipe1.public = true
        recipeService.save(recipe1)
        val recipe2 = Recipes.random(author1)
        recipe2.public = false
        recipeService.save(recipe2)

        val response = restTemplate.withBasicAuth(author2.userName, "secret")
            .getList<RecipeDto>("/recipe/popular")

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body).isNotNull().hasSize(1)
    }

    @Test
    fun `It should be possible to fetch the most recently created public recipes`() {
        val author1 = userService.createUser("test1@test.com", "woop", "secret")
        val author2 = userService.createUser("test2@test.com", "woop", "secret")
        val recipe1 = Recipes.random(author1)
        recipe1.public = false
        recipe1.createdAt = Instant.now()
        recipeService.save(recipe1)
        val recipe2 = Recipes.random(author1)
        recipe2.public = true
        recipe2.createdAt = Instant.now().minusSeconds(5)
        recipeService.save(recipe2)
        val recipe3 = Recipes.random(author2)
        recipe3.public = true
        recipe3.createdAt = Instant.now()
        recipeService.save(recipe3)

        val response = restTemplate.withBasicAuth(author2.userName, "secret")
            .getList<RecipeDto>("/recipe/recent")

        expectThat(response.statusCode)
            .isEqualTo(HttpStatus.OK)

        expectThat(response.body)
            .isNotNull()
            .hasSize(2)
            .map(RecipeDto::id)
            .containsExactly(recipe2.id, recipe3.id)
    }

    @Test
    fun `It should be possible to create a new recipe`() {
        val author = userService.createUser("test@test.com", "woop", "secret")
        categoryService.save(Category(name = "wawawa"))
        val request = CreateRecipeRequest(
            "woop",
            "woop",
            mutableListOf("woop"),
            true,
            Duration.ofHours(1),
            Duration.ofHours(2),
            3
        ).apply {
            tags = mutableSetOf("abc")
            categories = mutableSetOf("def")
        }

        val response = restTemplate.withBasicAuth(author.userName, "secret")
            .postForEntity<RecipeDto>("/recipe", request)

        expectThat(response.statusCode).isEqualTo(HttpStatus.CREATED)

        val recipe = response.body

        expectThat(recipe).isNotNull()
        expectThat(recipe!!.id).isNotNull()
    }

    @Test
    fun `It should be possible to replace an old recipe`() {
        val author = userService.createUser("test@test.com", "woop", "secret")
        val recipe = recipeService.save(Recipes.random(author))
        val request = CreateRecipeRequest(
            "wawawa",
            "wawawa",
            mutableListOf("woop"),
            true,
            Duration.ofHours(1),
            Duration.ofHours(2),
            3
        ).apply {
            tags = mutableSetOf("abc")
            categories = mutableSetOf("def")
        }

        restTemplate.withBasicAuth(author.userName, "secret")
            .put("/recipe/${recipe.id}", request)

        val result = recipeService.getById(recipe.id!!).get()

        expectThat(result.title).isEqualTo("wawawa")
        expectThat(result.description).isEqualTo("wawawa")
        expectThat(result.public).isTrue()
        expectThat(result.prepTime).isEqualTo(Duration.ofHours(1))
        expectThat(result.cookTime).isEqualTo(Duration.ofHours(2))
        expectThat(result.yield).isEqualTo(3)
    }

    @Test
    fun `It should be possible to update an old recipe`() {
        val author = userService.createUser("test", "woop", "secret")
        val recipe = recipeService.save(Recipes.random(author))
        val request = UpdateRecipeRequest(
            "wawawa",
        ).apply {
            tags = mutableSetOf("abc")
            categories = mutableSetOf("def")
        }

        restTemplate.withBasicAuth(author.userName, "secret")
            .patchForObject<Unit>("/recipe/${recipe.id}", request)

        val result = recipeService.getById(recipe.id!!).get()

        expectThat(result.title).isEqualTo(request.title)
        expectThat(result.description).isEqualTo(recipe.description)
    }

    @Test
    fun `It should be possible to fetch a recipe by id`() {
        val author = userService.createUser("test@test.com", "woop", "secret")
        val recipe = Recipes.random(author)
        recipeService.save(recipe)

        val response = restTemplate.withBasicAuth(author.userName, "secret")
            .getForEntity<RecipeDto>("/recipe/${recipe.id}")

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)

        expectThat(response.body).isNotNull()
        expectThat(response.body!!.id).isEqualTo(recipe.id)
    }

    @Test
    fun `It should return 404 if trying to fetch a non-existing recipe`() {
        val author = userService.createUser("test@test.com", "woop", "secret")

        val response = restTemplate.withBasicAuth(author.userName, "secret")
            .getForEntity<Unit>("/recipe/42")

        expectThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `It should be possible to delete a recipe by id`() {
        val author = userService.createUser("test@test.com", "woop", "secret")
        val recipe = Recipes.random(author)
        recipeService.save(recipe)

        restTemplate.withBasicAuth(author.userName, "secret")
            .delete("/recipe/${recipe.id}")

        expectThat(recipeService.getById(recipe.id!!)).isAbsent()
    }

    @Test
    fun `It should return 201 if trying to delete a non-existing recipe`() {
        val author = userService.createUser("test@test.com", "woop", "secret")

        expectCatching {
            restTemplate.withBasicAuth(author.userName, "secret")
                .delete("/recipe/42")
        }.isSuccess()
    }
}
