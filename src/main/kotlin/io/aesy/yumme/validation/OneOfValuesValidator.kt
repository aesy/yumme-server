package io.aesy.yumme.validation

import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class OneOfValuesValidator: ConstraintValidator<OneOfValues, Any> {
    private lateinit var values: Array<out String>
    private lateinit var message: String

    override fun initialize(annotation: OneOfValues) {
        this.values = annotation.value
        this.message = annotation.message

        if (message == "") {
            message = "Value must equal one of $values"
        }

        return super.initialize(annotation)
    }

    override fun isValid(
        input: Any?,
        context: ConstraintValidatorContext
    ): Boolean {
        if (values.isEmpty()) {
            return true
        }

        for (value in values) {
            if (value == input.toString()) {
                return true
            }
        }

        // Disable default ConstraintViolation so a customised message can be set instead
        context.disableDefaultConstraintViolation()

        context.buildConstraintViolationWithTemplate(message)
            .addConstraintViolation()

        return false
    }
}
