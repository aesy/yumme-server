package io.aesy.yumme.converter

import io.aesy.test.TestType
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.Duration

@TestType.Unit
class LongSecondsDurationJacksonConverterUnitTest {
    private val converter = LongSecondsDurationJacksonConverter()

    @Test
    fun `It should convert a long to a duration in seconds`() {
        val duration = converter.convert(7)

        expectThat(duration).isEqualTo(Duration.ofSeconds(7))
    }

    @Test
    fun `It should return null if given a null long`() {
        val duration = converter.convert(null)

        expectThat(duration).isEqualTo(null)
    }
}
