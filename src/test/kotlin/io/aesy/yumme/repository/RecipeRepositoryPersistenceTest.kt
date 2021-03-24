package io.aesy.yumme.repository

import io.aesy.test.TestType
import io.aesy.yumme.entity.Recipe
import io.aesy.yumme.entity.User
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import strikt.api.expectThat
import strikt.assertions.hasSize
import strikt.assertions.isNotNull

@TestType.Persistence
class RecipeRepositoryPersistenceTest {
    @Autowired
    private lateinit var recipeRepository: RecipeRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `It should be possible to persist a recipe`() {
        val user = User(email = "test@test.com", password = "secret")
        userRepository.save(user)
        val recipe = Recipe(title = "woop", author = user, description = "woop")
        recipeRepository.save(recipe)

        expectThat(recipe.id).isNotNull()
    }

    @Test
    fun `It should be possible to fetch all public recipes`() {
        val user = User(email = "test@test.com", password = "secret")
        userRepository.save(user)
        val recipe1 = Recipe(title = "woop", author = user, description = "woop")
        recipe1.public = true
        recipeRepository.save(recipe1)
        val recipe2 = Recipe(title = "woop", author = user, description = "woop")
        recipe2.public = false
        recipeRepository.save(recipe2)

        val recipes = recipeRepository.findAllPublic()

        expectThat(recipes).hasSize(1)
    }
}
