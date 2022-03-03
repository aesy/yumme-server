package io.aesy.yumme.repository

import io.aesy.test.TestType
import io.aesy.test.util.Recipes
import io.aesy.test.util.Users
import io.aesy.yumme.entity.Recipe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import strikt.api.expectThat
import strikt.assertions.*

@TestType.Persistence
class RecipeRepositoryPersistenceTest {
    @Autowired
    private lateinit var recipeRepository: RecipeRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `It should be possible to persist a recipe`() {
        val author = userRepository.save(Users.random())
        val recipe = recipeRepository.save(Recipes.random(author))

        expectThat(recipe.id).isNotNull()
    }

    @Test
    fun `It should be possible to fetch recipes by author`() {
        val author1 = userRepository.save(Users.random())
        val author2 = userRepository.save(Users.random())
        val recipe1 = recipeRepository.save(Recipes.random(author1))
        recipeRepository.save(Recipes.random(author2))

        val recipes = recipeRepository.findAllByAuthor(author1)

        expectThat(recipes)
            .map(Recipe::id)
            .containsExactly(recipe1.id)
    }

    @Test
    fun `It should be possible to fetch all public recipes`() {
        val author = userRepository.save(Users.random())
        val recipe1 = Recipes.random(author)
        recipe1.public = true
        recipeRepository.save(recipe1)
        val recipe2 = Recipes.random(author)
        recipe2.public = false
        recipeRepository.save(recipe2)

        val recipes = recipeRepository.findAllPublic()

        expectThat(recipes).hasSize(1)
    }
}
