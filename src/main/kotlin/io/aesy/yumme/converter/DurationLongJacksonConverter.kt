package io.aesy.yumme.converter

import com.fasterxml.jackson.databind.util.StdConverter
import java.time.Duration

class DurationLongJacksonConverter: StdConverter<Duration, Long>() {
    override fun convert(duration: Duration?): Long? {
        return duration?.toSeconds()
    }
}
