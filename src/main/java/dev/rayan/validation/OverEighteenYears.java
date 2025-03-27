package dev.rayan.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = OverEighteenYearsImpl.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface OverEighteenYears {
    String message() default "Invalid birth date!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
