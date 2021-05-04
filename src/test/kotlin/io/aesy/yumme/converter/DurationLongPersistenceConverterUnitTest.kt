package io.aesy.yumme.converter

import io.aesy.test.TestType
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.Duration

@TestType.Unit
class DurationLongPersistenceConverterUnitTest {
    private val converter = DurationLongPersistenceConverter()

    @Test
    fun `It should convert a duration object to seconds`() {
        val seconds = converter.convertToDatabaseColumn(Duration.ofSeconds(42))

        expectThat(seconds).isEqualTo(42)
    }

    @Test
    fun `It should return null if given a null duration object`() {
        val seconds = converter.convertToDatabaseColumn(null)

        expectThat(seconds).isEqualTo(null)
    }

    @Test
    fun `It should convert a long to a duration in seconds`() {
        val duration = converter.convertToEntityAttribute(7)

        expectThat(duration).isEqualTo(Duration.ofSeconds(7))
    }

    @Test
    fun `It should return null if given a null long`() {
        val duration = converter.convertToEntityAttribute(null)

        expectThat(duration).isEqualTo(null)
    }
}
