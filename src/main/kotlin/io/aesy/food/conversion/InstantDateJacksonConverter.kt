package io.aesy.food.conversion

import com.fasterxml.jackson.databind.util.StdConverter
import java.time.Instant
import java.time.format.DateTimeFormatter

class InstantDateJacksonConverter: StdConverter<Instant, String>() {
    override fun convert(instant: Instant?): String? {
        if (instant == null) {
            return null
        }

        return DateTimeFormatter.ISO_INSTANT.format(instant)
    }
}
