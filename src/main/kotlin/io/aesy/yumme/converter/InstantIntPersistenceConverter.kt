package io.aesy.yumme.converter

import java.time.Instant
import javax.persistence.AttributeConverter

class InstantIntPersistenceConverter: AttributeConverter<Instant, Long> {
    override fun convertToDatabaseColumn(instant: Instant?): Long? {
        return instant?.epochSecond
    }

    override fun convertToEntityAttribute(seconds: Long?): Instant? {
        if (seconds == null) {
            return null
        }

        return Instant.ofEpochSecond(seconds)
    }
}
