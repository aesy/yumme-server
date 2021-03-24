package io.aesy.yumme.converter

import io.aesy.test.TestType
import io.aesy.yumme.conversion.DurationLongJacksonConverter
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.Duration

@TestType.Unit
class DurationLongJacksonConverterUnitTest {
    private val converter = DurationLongJacksonConverter()

    @Test
    fun `It should convert a duration to seconds`() {
        val seconds = converter.convert(Duration.ofSeconds(7))

        expectThat(seconds).isEqualTo(7)
    }

    @Test
    fun `It should return a 0 if given a null duration`() {
        val duration = converter.convert(null)

        expectThat(duration).isEqualTo(0)
    }
}
