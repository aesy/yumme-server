package io.aesy.yumme.conversion

import com.fasterxml.jackson.databind.util.StdConverter
import java.time.Duration

class DurationLongJacksonConverter: StdConverter<Duration, Long>() {
    override fun convert(duration: Duration?): Long {
        if (duration == null) {
            return 0
        }

        return duration.toSeconds()
    }
}
