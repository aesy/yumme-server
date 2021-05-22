package io.aesy.yumme.converter

import com.fasterxml.jackson.databind.util.StdConverter
import java.time.Duration

class LongSecondsDurationJacksonConverter: StdConverter<Long, Duration>() {
    override fun convert(seconds: Long?): Duration? {
        if (seconds == null) {
            return null
        }

        return Duration.ofSeconds(seconds)
    }
}
