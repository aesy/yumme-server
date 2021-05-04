package io.aesy.yumme.converter

import io.aesy.test.TestType
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.Instant

@TestType.Unit
class InstantIntPersistenceConverterUnitTest {
    private val converter = InstantIntPersistenceConverter()

    @Test
    fun `It should convert an instant object to seconds`() {
        val seconds = converter.convertToDatabaseColumn(Instant.ofEpochSecond(42))

        expectThat(seconds).isEqualTo(42)
    }

    @Test
    fun `It should return null if given a null instant object`() {
        val seconds = converter.convertToDatabaseColumn(null)

        expectThat(seconds).isEqualTo(null)
    }

    @Test
    fun `It should convert a int to an instant in seconds`() {
        val instant = converter.convertToEntityAttribute(7)

        expectThat(instant).isEqualTo(Instant.ofEpochSecond(7))
    }

    @Test
    fun `It should return null if given a null int`() {
        val instant = converter.convertToEntityAttribute(null)

        expectThat(instant).isEqualTo(null)
    }
}
