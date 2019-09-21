package io.aesy.food.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = OneOfGroupsValidator.class)
@Target(TYPE)
@Retention(RUNTIME)
@Repeatable(OneOfGroups.List.class)
public @interface OneOfGroups {
    Class<?>[] value();
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    @Target(TYPE)
    @Retention(RUNTIME)
    @interface List {
        OneOfGroups[] value();
    }
}
