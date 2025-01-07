package dev.rayan.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OverEighteenYearsImpl implements ConstraintValidator<OverEighteenYears, LocalDate> {

    @Override
    public boolean isValid(final LocalDate birthDate, ConstraintValidatorContext constraintValidatorContext) {
        return ChronoUnit.YEARS.between(birthDate, LocalDate.now()) > 17;
    }
}

