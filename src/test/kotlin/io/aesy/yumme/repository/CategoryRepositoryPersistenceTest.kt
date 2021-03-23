package io.aesy.yumme.repository

import io.aesy.test.TestType
import io.aesy.yumme.entity.Category
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import strikt.api.expectThat
import strikt.assertions.isNotNull
import strikt.java.isPresent

@TestType.Persistence
class CategoryRepositoryPersistenceTest {
    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    @Test
    fun `It should be possible to persist a category`() {
        val category = Category(name = "woop")
        categoryRepository.save(category)

        expectThat(category.id).isNotNull()
    }

    @Test
    fun `It should be possible to fetch a category by name`() {
        val name = "woop"
        val category = Category(name = name)
        categoryRepository.save(category)

        val result = categoryRepository.findByName(name)

        expectThat(result).isPresent()
    }
}
