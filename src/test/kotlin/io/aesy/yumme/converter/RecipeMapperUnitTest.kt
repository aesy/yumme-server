package io.aesy.yumme.converter

import io.aesy.test.TestType
import com.ninjasquad.springmockk.MockkBean
import io.aesy.yumme.conversion.*
import io.aesy.yumme.dto.RecipeDto
import io.aesy.yumme.entity.*
import io.aesy.yumme.repository.RatingRepository
import io.aesy.yumme.service.RatingService
import io.mockk.every
import org.junit.jupiter.api.Test
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import strikt.api.expectThat
import strikt.assertions.*

@TestType.Unit
@SpringBootTest(
    classes = [
        MapperConfiguration::class,
        RecipeMapperConfigurer::class,
        CategoryMapperConfigurer::class,
        TagMapperConfigurer::class,
        RatingService::class
    ]
)
class RecipeMapperUnitTest {
    @Autowired
    private lateinit var modelMapper: ModelMapper

    @MockkBean
    private lateinit var ratingRepository: RatingRepository

    @Test
    fun `It should be possible to convert a Recipe entity to a Recipe DTO`() {
        val user = User(email = "test@test.com", password = "secret")
        val recipe = Recipe(title = "title", description = "description", author = user)

        every { ratingRepository.getAverageByRecipe(recipe) } returns 0.0
        every { ratingRepository.getCountByRecipe(recipe) } returns 0

        val dto = modelMapper.map(recipe, RecipeDto::class.java)

        expectThat(dto.title).isEqualTo(recipe.title)
        expectThat(dto.description).isEqualTo(recipe.description)
        expectThat(dto.rating).isNotNull()
        expectThat(dto.rating!!.average).isEqualTo(0.0)
        expectThat(dto.rating!!.count).isEqualTo(0)
        expectThat(dto.tags).isEmpty()
        expectThat(dto.categories).isEmpty()
    }

    @Test
    fun `It should be possible to convert a Recipe entity with ratings to a Recipe DTO`() {
        val user = User(email = "test@test.com", password = "secret")
        val recipe = Recipe(title = "title", description = "description", author = user)

        every { ratingRepository.getAverageByRecipe(recipe) } returns 2.5
        every { ratingRepository.getCountByRecipe(recipe) } returns 2

        val dto = modelMapper.map(recipe, RecipeDto::class.java)

        expectThat(dto.rating).isNotNull()
        expectThat(dto.rating!!.average).isEqualTo(2.5)
        expectThat(dto.rating!!.count).isEqualTo(2)
    }

    @Test
    fun `It should be possible to convert a Recipe entity with tags to a Recipe DTO`() {
        val user = User(email = "test@test.com", password = "secret")
        val recipe = Recipe(title = "title", description = "description", author = user)
        recipe.tags.add(Tag(name = "first", recipe = recipe))
        recipe.tags.add(Tag(name = "second", recipe = recipe))

        every { ratingRepository.getAverageByRecipe(recipe) } returns 0.0
        every { ratingRepository.getCountByRecipe(recipe) } returns 0

        val dto = modelMapper.map(recipe, RecipeDto::class.java)

        expectThat(dto.tags).containsExactly("first", "second")
    }

    @Test
    fun `It should be possible to convert a Recipe entity with categories to a Recipe DTO`() {
        val user = User(email = "test@test.com", password = "secret")
        val recipe = Recipe(title = "title", description = "description", author = user)
        recipe.categories.add(Category(name = "first"))
        recipe.categories.add(Category(name = "second"))

        every { ratingRepository.getAverageByRecipe(recipe) } returns 0.0
        every { ratingRepository.getCountByRecipe(recipe) } returns 0

        val dto = modelMapper.map(recipe, RecipeDto::class.java)

        expectThat(dto.categories).containsExactly("first", "second")
    }
}
