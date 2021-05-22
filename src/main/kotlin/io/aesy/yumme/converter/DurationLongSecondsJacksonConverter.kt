package io.aesy.yumme.converter

import com.fasterxml.jackson.databind.util.StdConverter
import java.time.Duration

class DurationLongSecondsJacksonConverter: StdConverter<Duration, Long>() {
    override fun convert(duration: Duration?): Long? {
        return duration?.toSeconds()
    }
}
