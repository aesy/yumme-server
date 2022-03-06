package io.aesy.yumme.controller

import io.aesy.test.TestType
import io.aesy.test.util.Recipes
import io.aesy.test.util.Users.createUser
import io.aesy.yumme.dto.ImageUploadDto
import io.aesy.yumme.entity.RecipeHasImageUpload.Type
import io.aesy.yumme.service.RecipeService
import io.aesy.yumme.service.UserService
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.*
import org.springframework.core.io.ClassPathResource
import org.springframework.http.*
import org.springframework.util.LinkedMultiValueMap
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.*
import java.io.ByteArrayInputStream
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO

@TestType.RestApi
class ImageRestApiTest {
    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var recipeService: RecipeService

    @ParameterizedTest
    @ValueSource(strings = ["testData/large_image.png", "testData/large_image.jpg"])
    fun `It should be possible to upload an image`(resource: String) {
        val author = userService.createUser("test", "woop", "secret")
        val recipe = recipeService.save(Recipes.random(author))

        val parameters = LinkedMultiValueMap<String, Any>()
        parameters.add("file", ClassPathResource(resource))
        val headers = HttpHeaders()
        headers.contentType = MediaType.MULTIPART_FORM_DATA
        val entity = HttpEntity(parameters, headers)

        val response = restTemplate.withBasicAuth(author.userName, "secret")
            .postForEntity<ImageUploadDto>("/recipe/${recipe.id}/image", entity)

        expectThat(response.statusCode).isEqualTo(HttpStatus.OK)
        expectThat(response.body?.name).isNotNull()
    }

    @Test
    fun `It should reject images that are too small`() {
        val author = userService.createUser("test", "woop", "secret")
        val recipe = recipeService.save(Recipes.random(author))

        val parameters = LinkedMultiValueMap<String, Any>()
        parameters.add("file", ClassPathResource("testData/small_image.png"))
        val headers = HttpHeaders()
        headers.contentType = MediaType.MULTIPART_FORM_DATA
        val entity = HttpEntity(parameters, headers)

        val response = restTemplate.withBasicAuth(author.userName, "secret")
            .postForEntity<Unit>("/recipe/${recipe.id}/image", entity)

        expectThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `It should be possible to view a thumbnail within reasonable time`() {
        val author = userService.createUser("test", "woop", "secret")
        val recipe = recipeService.save(Recipes.random(author))

        val parameters = LinkedMultiValueMap<String, Any>()
        parameters.add("file", ClassPathResource("testData/large_image.png"))
        val headers = HttpHeaders()
        headers.contentType = MediaType.MULTIPART_FORM_DATA
        val entity = HttpEntity(parameters, headers)

        val name = restTemplate.withBasicAuth(author.userName, "secret")
            .postForObject<ImageUploadDto>("/recipe/${recipe.id}/image", entity)!!
            .name

        await()
            .pollInterval(3, TimeUnit.SECONDS)
            .atMost(1, TimeUnit.MINUTES)
            .untilAsserted {
                val response = restTemplate.withBasicAuth(author.userName, "secret")
                    .getForEntity<ByteArray>("/recipe/${recipe.id}/image/$name?size=thumbnail")

                expectThat(response.statusCode)
                    .isEqualTo(HttpStatus.OK)

                expectThat(response.headers)
                    .containsKey(HttpHeaders.CACHE_CONTROL)
                    .withValue(HttpHeaders.CONTENT_TYPE) { isEqualTo(listOf(MediaType.IMAGE_PNG_VALUE)) }

                expectThat(response.body)
                    .isNotNull()
                    .get { ByteArrayInputStream(this).use(ImageIO::read) }
                    .get {
                        expectThat(width).isEqualTo(Type.THUMBNAIL.dimensions.width)
                        expectThat(height).isEqualTo(Type.THUMBNAIL.dimensions.height)
                    }
            }
    }

    @Test
    fun `It should be possible to delete an uploaded image`() {
        val author = userService.createUser("test", "woop", "secret")
        val recipe = recipeService.save(Recipes.random(author))

        val parameters = LinkedMultiValueMap<String, Any>()
        parameters.add("file", ClassPathResource("testData/large_image.png"))
        val headers = HttpHeaders()
        headers.contentType = MediaType.MULTIPART_FORM_DATA
        val entity = HttpEntity(parameters, headers)

        val name = restTemplate.withBasicAuth(author.userName, "secret")
            .postForObject<ImageUploadDto>("/recipe/${recipe.id}/image", entity)!!
            .name

        expectCatching {
            restTemplate.withBasicAuth(author.userName, "secret")
                .delete("/recipe/${recipe.id}/image/$name")
        }.isSuccess()
    }
}
