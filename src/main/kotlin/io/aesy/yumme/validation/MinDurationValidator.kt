package io.aesy.yumme.validation

import java.time.Duration
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class MinDurationValidator: ConstraintValidator<MinDuration, Duration> {
    private lateinit var duration: Duration
    private lateinit var message: String

    override fun initialize(annotation: MinDuration) {
        this.duration = Duration.of(annotation.amount, annotation.unit)
        this.message = annotation.message

        if (message == "") {
            message = "Duration must be longer than ${annotation.amount} ${annotation.unit.name.toLowerCase()}"
        }

        return super.initialize(annotation)
    }

    override fun isValid(
        input: Duration?,
        context: ConstraintValidatorContext
    ): Boolean {
        val isValid = when (input) {
            null -> true
            else -> input >= duration
        }

        if (isValid) {
            return true
        }

        // Disable default ConstraintViolation so a customised message can be set instead
        context.disableDefaultConstraintViolation()

        context.buildConstraintViolationWithTemplate(message)
            .addConstraintViolation()

        return false
    }
}
