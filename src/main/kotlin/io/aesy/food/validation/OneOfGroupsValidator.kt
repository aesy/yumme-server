package io.aesy.food.validation

import javax.validation.*
import kotlin.reflect.KClass

class OneOfGroupsValidator(
    private val validator: Validator
): ConstraintValidator<OneOfGroups, Any> {
    private lateinit var groups: Array<out KClass<out Any>>
    private lateinit var message: String

    override fun initialize(annotation: OneOfGroups) {
        this.groups = annotation.value
        this.message = annotation.message

        if (message == "") {
            message = "At least one of groups $groups must be valid"
        }

        return super.initialize(annotation)
    }

    override fun isValid(
        input: Any?,
        context: ConstraintValidatorContext
    ): Boolean {
        if (groups.isEmpty()) {
            return true
        }

        for (group in groups) {
            val violations = validator.validate(input, group.java)

            if (violations.isEmpty()) {
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
