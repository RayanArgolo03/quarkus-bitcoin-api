package dev.rayan.validation;

import dev.rayan.enums.BaseEnum;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.apache.poi.ss.formula.functions.T;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = EnumValidatorImpl.class)
public @interface EnumValidator {
    Class<? extends BaseEnum<?>> enumClass();

    String message() default "Invalid value!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}


