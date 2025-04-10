
package dev.rayan.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = EqualsPasswordValidatorImpl.class)
public @interface EqualsPassword {

    String message() default "Confirmed password should be equals to new password!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}