package io.aesy.yumme.service

import io.aesy.test.TestType
import io.aesy.yumme.repository.CategoryRepository
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.justRun
import io.mockk.verify
import org.junit.jupiter.api.Test

@TestType.Unit
class CategoryServiceUnitTest {
    @MockK
    private lateinit var repository: CategoryRepository

    @InjectMockKs
    private lateinit var service: CategoryService

    @Test
    fun `It should be able to delete by id`() {
        val id = 1L

        justRun { repository.deleteById(id) }

        service.delete(id)

        verify { repository.deleteById(id) }
    }
}
