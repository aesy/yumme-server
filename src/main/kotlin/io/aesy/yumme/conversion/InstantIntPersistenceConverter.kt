package io.aesy.yumme.conversion

import java.time.Instant
import javax.persistence.AttributeConverter

class InstantIntPersistenceConverter: AttributeConverter<Instant, Long> {
    override fun convertToDatabaseColumn(instant: Instant?): Long? {
        if (instant == null) {
            return 0
        }

        return instant.epochSecond
    }

    override fun convertToEntityAttribute(seconds: Long?): Instant? {
        if (seconds == null) {
            return Instant.ofEpochSecond(0)
        }

        return Instant.ofEpochSecond(seconds)
    }
}
