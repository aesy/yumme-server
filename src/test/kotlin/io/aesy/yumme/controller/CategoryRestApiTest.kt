package io.aesy.yumme.controller

import io.aesy.yumme.util.Recipes
import io.aesy.test.TestType
import io.aesy.yumme.dto.CategoryDto
import io.aesy.yumme.entity.Category
import io.aesy.yumme.service.*
import io.aesy.yumme.util.HTTP.getList
import io.aesy.yumme.util.Users.createUser
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus
import strikt.api.expectThat
import strikt.assertions.*

@TestType.RestApi
class CategoryRestApiTest {
    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var categoryService: CategoryService

    @Autowired
    private lateinit var recipeService: RecipeService

    @Autowired
    private lateinit var userService: UserService

    @Test
    fun `It should be possible to fetch all categories`() {
        val user = userService.createUser("test", "woop", "secret")
        categoryService.save(Category(name = "one"))
        categoryService.save(Category(name = "two"))
        categoryService.save(Category(name = "three"))

        val response = restTemplate.withBasicAuth(user.userName, "secret")
            .getList<CategoryDto>("/category")

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val categories = response.body!!

        expectThat(categories).hasSize(3)
    }

    @Test
    fun `It should be possible to fetch categories by recipe`() {
        val author = userService.createUser("test@test.com", "woop", "secret")
        val category = categoryService.save(Category(name = "one"))
        categoryService.save(Category(name = "two")) // Should not be included in response
        val recipe = Recipes.random(author)
        recipeService.save(recipe)
        recipe.categories.add(category)
        recipeService.save(recipe)

        val response = restTemplate.withBasicAuth(author.userName, "secret")
            .getList<CategoryDto>("/recipe/${recipe.id}/category")

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val categories = response.body!!.toList()

        expectThat(categories)
            .map(CategoryDto::name)
            .containsExactly("one")
    }

    @Test
    fun `It should return 404 if trying to fetch categories by unknown recipe`() {
        val user = userService.createUser("test", "woop", "secret")

        val response = restTemplate.withBasicAuth(user.userName, "secret")
            .getForEntity<Unit>("/recipe/42/category")

        expectThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }
}
