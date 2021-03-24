package io.aesy.yumme.conversion

import com.fasterxml.jackson.databind.util.StdConverter
import java.time.Duration

class LongDurationJacksonConverter: StdConverter<Long, Duration>() {
    override fun convert(seconds: Long?): Duration {
        if (seconds == null) {
            return Duration.ZERO
        }

        return Duration.ofSeconds(seconds)
    }
}
