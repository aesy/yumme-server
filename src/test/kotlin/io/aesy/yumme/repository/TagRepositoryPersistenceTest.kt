package io.aesy.yumme.repository

import io.aesy.test.TestType
import io.aesy.test.util.Recipes
import io.aesy.test.util.Users
import io.aesy.yumme.entity.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import strikt.api.expectThat
import strikt.assertions.*
import strikt.java.isPresent

@TestType.Persistence
class TagRepositoryPersistenceTest {
    @Autowired
    private lateinit var tagRepository: TagRepository

    @Autowired
    private lateinit var recipeRepository: RecipeRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `It should be possible to persist a tag`() {
        val author = userRepository.save(Users.random())
        val recipe = recipeRepository.save(Recipes.random(author))
        val tag = tagRepository.save(Tag(name = "woop", recipe = recipe))

        expectThat(tag.id).isNotNull()
    }

    @Test
    fun `It should be possible to fetch tag by name and recipe`() {
        val author = userRepository.save(Users.random())
        val recipe1 = recipeRepository.save(Recipes.random(author))
        val recipe2 = recipeRepository.save(Recipes.random(author))
        val tag = tagRepository.save(Tag(name = "woop", recipe = recipe1))
        tagRepository.save(Tag(name = "woop", recipe = recipe2)) // Should not be included in result

        val result = tagRepository.findByNameAndRecipe("woop", recipe1)

        expectThat(result)
            .isPresent()
            .get { expectThat(id).isEqualTo(tag.id) }
    }

    @Test
    fun `It should be possible to fetch all tags in pages`() {
        val author = userRepository.save(Users.random())
        val recipe1 = recipeRepository.save(Recipes.random(author))
        val recipe2 = recipeRepository.save(Recipes.random(author))
        tagRepository.save(Tag(name = "woop", recipe = recipe1))
        tagRepository.save(Tag(name = "woop", recipe = recipe2))

        val result = tagRepository.findAll(PageRequest.of(0, 1))

        expectThat(result).hasSize(1)
    }

    @Test
    fun `It should be possible to fetch tags by recipe`() {
        val author = userRepository.save(Users.random())
        val recipe1 = recipeRepository.save(Recipes.random(author))
        val recipe2 = recipeRepository.save(Recipes.random(author))
        val tag = tagRepository.save(Tag(name = "woop", recipe = recipe1))
        tagRepository.save(Tag(name = "woop", recipe = recipe2)) // Should not be included in result

        val result = tagRepository.findAllByRecipe(recipe1)

        expectThat(result)
            .map(Tag::id)
            .containsExactly(tag.id)
    }
}
