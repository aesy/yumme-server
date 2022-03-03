package io.aesy.yumme.repository

import io.aesy.test.TestType
import io.aesy.test.util.Recipes
import io.aesy.test.util.Users
import io.aesy.yumme.entity.Collection
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import strikt.api.expectThat
import strikt.assertions.*

@TestType.Persistence
class CollectionRepositoryPersistenceTest {
    @Autowired
    private lateinit var collectionRepository: CollectionRepository

    @Autowired
    private lateinit var recipeRepository: RecipeRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `It should be possible to persist a collection`() {
        val author = userRepository.save(Users.random())
        val recipe = recipeRepository.save(Recipes.random(author))
        val collection = collectionRepository.save(Collection(title = "woop", owner = author))
        collection.recipes.add(recipe)
        collectionRepository.save(collection)

        expectThat(collection.id).isNotNull()
    }

    @Test
    fun `It should be possible to fetch collections by owner`() {
        val user1 = userRepository.save(Users.random())
        val user2 = userRepository.save(Users.random())
        val collection = collectionRepository.save(Collection(title = "woop", owner = user1))
        collectionRepository.save(Collection(title = "woop", owner = user2)) // Should not be included

        val result = collectionRepository.findByOwner(user1)

        expectThat(result)
            .map(Collection::id)
            .containsExactly(collection.id)
    }
}
