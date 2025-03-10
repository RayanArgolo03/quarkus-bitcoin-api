package dev.rayan.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class OverEighteenYearsImpl implements ConstraintValidator<OverEighteenYears, LocalDate> {

    @Override
    public boolean isValid(final LocalDate birthDate, final ConstraintValidatorContext constraintValidatorContext) {
        return ChronoUnit.YEARS.between(birthDate, LocalDate.now()) > 16;
    }
}

