package io.aesy.yumme.conversion

import java.time.Duration
import javax.persistence.AttributeConverter

class DurationLongPersistenceConverter: AttributeConverter<Duration, Long> {
    override fun convertToDatabaseColumn(duration: Duration?): Long {
        if (duration == null) {
            return 0
        }

        return duration.toSeconds()
    }

    override fun convertToEntityAttribute(seconds: Long?): Duration {
        if (seconds == null) {
            return Duration.ZERO
        }

        return Duration.ofSeconds(seconds)
    }
}
