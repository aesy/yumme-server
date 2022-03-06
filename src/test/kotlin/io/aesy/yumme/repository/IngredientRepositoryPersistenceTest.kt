package io.aesy.yumme.repository

import io.aesy.test.TestType
import io.aesy.test.util.Recipes
import io.aesy.test.util.Users
import io.aesy.yumme.entity.Ingredient
import io.aesy.yumme.repository.matcher.IngredientMatcher
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import strikt.api.expectThat
import strikt.assertions.*
import strikt.java.isAbsent
import strikt.java.isPresent

@TestType.Persistence
class IngredientRepositoryPersistenceTest {
    @Autowired
    private lateinit var recipeRepository: RecipeRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var ingredientRepository: IngredientRepository

    @Test
    fun `It should be possible to persist an ingredient`() {
        val ingredient = Ingredient(name = "woop")
        ingredientRepository.save(ingredient)

        expectThat(ingredient.id).isNotNull()
    }

    @Test
    fun `It should be possible to fetch an ingredient by name`() {
        val name = "woop"
        val ingredient = Ingredient(name = name)
        ingredientRepository.save(ingredient)

        val resultSuccess = ingredientRepository.findByName(name)
        val resultFailure = ingredientRepository.findByName("non-match")

        expectThat(resultSuccess).isPresent()
        expectThat(resultFailure).isAbsent()
    }

    @Test
    fun `It should be possible to search ingredients by name`() {
        val name = "woop"
        val ingredient = Ingredient(name = name)
        ingredientRepository.save(ingredient)

        val resultSuccess = ingredientRepository.findAll(IngredientMatcher.hasNameLike("oo"))
        val resultFailure = ingredientRepository.findAll(IngredientMatcher.hasNameLike("non-match"))

        expectThat(resultSuccess).isNotEmpty()
        expectThat(resultFailure).isEmpty()
    }

    @Test
    fun `It should be possible to search ingredients by recipe`() {
        val ingredient = Ingredient(name = "woop")
        ingredientRepository.save(ingredient)
        val author = userRepository.save(Users.random())
        val recipe1 = recipeRepository.save(Recipes.random(author))
        recipe1.ingredients.add(ingredient)
        recipeRepository.save(recipe1)
        val recipe2 = recipeRepository.save(Recipes.random(author))

        val resultSuccess = ingredientRepository.findAll(IngredientMatcher.isInRecipe(recipe1))
        val resultFailure = ingredientRepository.findAll(IngredientMatcher.isInRecipe(recipe2))

        expectThat(resultSuccess).hasSize(1)
        expectThat(resultFailure).isEmpty()
    }
}
