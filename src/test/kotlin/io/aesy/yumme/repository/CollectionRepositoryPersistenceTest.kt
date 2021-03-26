package io.aesy.yumme.repository

import io.aesy.test.TestType
import io.aesy.yumme.entity.Collection
import io.aesy.yumme.entity.User
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import strikt.api.expectThat
import strikt.assertions.*

@TestType.Persistence
class CollectionRepositoryPersistenceTest {
    @Autowired
    private lateinit var collectionRepository: CollectionRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `It should be possible to persist a collection`() {
        val user = User(email = "test@test.com", password = "secret")
        userRepository.save(user)

        val collection = Collection(title = "woop", owner = user)
        collectionRepository.save(collection)

        expectThat(collection.id).isNotNull()
    }

    @Test
    @Disabled("Filepeek dependency is unavailable, see github.com/robfletcher/strikt/issues/242")
    fun `It should be possible to fetch collections by owner`() {
        val user = User(email = "test@test.com", password = "secret")
        userRepository.save(user)

        val collection = Collection(title = "woop", owner = user)
        collectionRepository.save(collection)

        val collections = collectionRepository.findByOwner(user)

        expectThat(collections).map(Collection::id).containsExactly(collection.id)
    }
}
