package io.aesy.yumme.service

import io.aesy.test.TestType
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import strikt.api.expectThat
import strikt.assertions.contains
import strikt.assertions.hasLength
import kotlin.math.min

@TestType.Unit
class SlugServiceUnitTest {
    @ParameterizedTest
    @ValueSource(ints = [5, 10, 100])
    fun `It should generate a slug of a specific length based on a given name`(limit: Int) {
        val slugService = SlugService()
        val name = "base-name"
        val slug = slugService.generateSlug(name, limit)

        expectThat(slug)
            .contains(name.substring(0, min(limit, name.length)))
            .hasLength(limit)
    }
}
