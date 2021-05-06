package io.aesy.yumme.controller

import io.aesy.test.TestType
import io.aesy.yumme.dto.*
import io.aesy.yumme.entity.Collection
import io.aesy.yumme.service.*
import io.aesy.yumme.util.HTTP.getList
import io.aesy.yumme.util.Recipes
import io.aesy.yumme.util.Users.createUser
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpStatus
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.*
import strikt.java.isAbsent

@TestType.RestApi
class CollectionRestApiTest {
    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var collectionService: CollectionService

    @Autowired
    private lateinit var recipeService: RecipeService

    @Autowired
    private lateinit var userService: UserService

    @Test
    fun `It should be possible to list the authenticated users collections`() {
        val user1 = userService.createUser("test1@test.com", "woop", "secret")
        val user2 = userService.createUser("test2@test.com", "woop", "secret")
        val collection1 = collectionService.save(Collection(title = "woop", owner = user1))
        collectionService.save(Collection(title = "woop", owner = user2))

        val response = restTemplate.withBasicAuth(user1.userName, "secret")
            .getList<CollectionDto>("/collection")

        expectThat(response.statusCode)
            .isEqualTo(HttpStatus.OK)

        expectThat(response.body)
            .isNotNull()
            .map(CollectionDto::id)
            .containsExactly(collection1.id)
    }

    @Test
    fun `It should be possible to create a new collection`() {
        val author = userService.createUser("test@test.com", "woop", "secret")
        val recipe = recipeService.save(Recipes.random(author))
        val request = CollectionDto(id = null, title = "wawawa", recipes = mutableSetOf(recipe.id!!))

        val response = restTemplate.withBasicAuth(author.userName, "secret")
            .postForEntity<CollectionDto>("/collection", request)

        expectThat(response.statusCode).isEqualTo(HttpStatus.CREATED)

        val collection = response.body

        expectThat(collection).isNotNull()
        expectThat(collection!!.id).isNotNull()
        expectThat(collection.title).isEqualTo("wawawa")
        expectThat(collection.recipes).containsExactly(recipe.id)
    }

    @Test
    fun `It should be possible to replace an old collection`() {
        val author = userService.createUser("test@test.com", "woop", "secret")
        val recipe = recipeService.save(Recipes.random(author))
        val collection = collectionService.save(Collection(title = "woop", owner = author))
        val request = UpdateCollectionRequest("wawawa")
        request.recipes.add(recipe.id!!)

        restTemplate.withBasicAuth(author.userName, "secret")
            .put("/collection/${collection.id}", request)

        val result = collectionService.getById(collection.id!!).get()

        expectThat(result.title).isEqualTo("wawawa")
    }

    @Test
    fun `It should be possible to delete a collection by id`() {
        val author = userService.createUser("test@test.com", "woop", "secret")
        val recipe = Recipes.random(author)
        val collection = Collection(title = "woop", owner = author)
        recipeService.save(recipe)
        collectionService.save(collection)

        restTemplate.withBasicAuth(author.userName, "secret")
            .delete("/collection/${collection.id}")

        expectThat(collectionService.getById(collection.id!!)).isAbsent()
    }

    @Test
    fun `It should return 201 if trying to delete a non-existing collection`() {
        val user = userService.createUser("test", "woop", "secret")

        expectCatching {
            restTemplate.withBasicAuth(user.userName, "secret")
                .delete("/collection/42")
        }.isSuccess()
    }
}
