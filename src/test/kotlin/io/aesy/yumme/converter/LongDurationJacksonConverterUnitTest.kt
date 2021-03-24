package io.aesy.yumme.converter

import io.aesy.test.TestType
import io.aesy.yumme.conversion.LongDurationJacksonConverter
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.Duration

@TestType.Unit
class LongDurationJacksonConverterUnitTest {
    private val converter = LongDurationJacksonConverter()

    @Test
    fun `It should convert a long to a duration in seconds`() {
        val duration = converter.convert(7)

        expectThat(duration).isEqualTo(Duration.ofSeconds(7))
    }

    @Test
    fun `It should return a duration object of 0 seconds if given a null long`() {
        val duration = converter.convert(null)

        expectThat(duration).isEqualTo(Duration.ZERO)
    }
}
