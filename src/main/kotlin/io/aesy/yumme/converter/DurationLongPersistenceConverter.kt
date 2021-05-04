package io.aesy.yumme.converter

import java.time.Duration
import javax.persistence.AttributeConverter

class DurationLongPersistenceConverter: AttributeConverter<Duration, Long> {
    override fun convertToDatabaseColumn(duration: Duration?): Long? {
        return duration?.toSeconds()
    }

    override fun convertToEntityAttribute(seconds: Long?): Duration? {
        if (seconds == null) {
            return null
        }

        return Duration.ofSeconds(seconds)
    }
}
