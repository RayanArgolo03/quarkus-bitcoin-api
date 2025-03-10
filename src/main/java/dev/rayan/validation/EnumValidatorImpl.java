package dev.rayan.validation;

import dev.rayan.enums.BaseEnum;
import dev.rayan.utils.StringToLowerUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumValidatorImpl implements ConstraintValidator<EnumValidator, CharSequence> {

    private List<String> acceptedValues;

    @Override
    public void initialize(EnumValidator annotation) {
        acceptedValues = StringToLowerUtils.toLower(
                Stream.of(annotation.enumClass().getEnumConstants())
                        .map(BaseEnum::getValue)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public boolean isValid(final CharSequence value, final ConstraintValidatorContext context) {
        if (value == null) return false;
        return acceptedValues.contains(value.toString().toLowerCase());
    }
}

