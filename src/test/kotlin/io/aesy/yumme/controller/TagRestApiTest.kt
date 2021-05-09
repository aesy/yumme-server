package io.aesy.yumme.controller

import io.aesy.test.TestType
import io.aesy.yumme.dto.TagDto
import io.aesy.yumme.entity.Tag
import io.aesy.yumme.service.*
import io.aesy.yumme.util.HTTP.getList
import io.aesy.yumme.util.Recipes
import io.aesy.yumme.util.Users.createAdmin
import io.aesy.yumme.util.Users.createUser
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import strikt.api.expectThat
import strikt.assertions.*

@TestType.RestApi
class TagRestApiTest {
    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var tagService: TagService

    @Autowired
    private lateinit var recipeService: RecipeService

    @Autowired
    private lateinit var userService: UserService

    @Test
    fun `It should be possible to fetch all tags as an admin`() {
        val author = userService.createAdmin("test@test.com", "woop", "secret")
        val recipe = recipeService.save(Recipes.random(author))
        tagService.save(Tag(name = "one", recipe = recipe))
        tagService.save(Tag(name = "two", recipe = recipe))
        tagService.save(Tag(name = "three", recipe = recipe))

        val response = restTemplate.withBasicAuth(author.userName, "secret")
            .getList<TagDto>("/tag")

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val tags = response.body!!

        expectThat(tags).hasSize(3)
    }

    @Test
    fun `It should not be possible to fetch all tags as a user`() {
        val user = userService.createUser("test", "woop", "secret")

        val response = restTemplate.withBasicAuth(user.userName, "secret")
            .getList<Unit>("/tag")

        expectThat(response.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
    }

    @Test
    fun `It should be possible to fetch tags by recipe`() {
        val author1 = userService.createUser("test1@test.com", "woop", "secret")
        val author2 = userService.createUser("test2@test.com", "woop", "secret")
        val recipe1 = recipeService.save(Recipes.random(author1))
        val recipe2 = recipeService.save(Recipes.random(author2))
        tagService.save(Tag(name = "one", recipe = recipe1))
        tagService.save(Tag(name = "two", recipe = recipe2)) // Should not be included in response

        val response = restTemplate.withBasicAuth(author1.userName, "secret")
            .getList<TagDto>("/recipe/${recipe1.id}/tag")

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val tags = response.body!!.toList()

        expectThat(tags)
            .map(TagDto::name)
            .containsExactly("one")
    }
}
