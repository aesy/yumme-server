package io.aesy.food.validation

import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class EqualsValidator: ConstraintValidator<Equals, Any> {
    private lateinit var value: String
    private lateinit var message: String

    override fun initialize(annotation: Equals) {
        this.value = annotation.value
        this.message = annotation.message

        if (message == "") {
            message = "Value does not equal '$value'"
        }

        return super.initialize(annotation)
    }

    override fun isValid(
        input: Any?,
        context: ConstraintValidatorContext
    ): Boolean {
        val isValid = value == input.toString()

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
