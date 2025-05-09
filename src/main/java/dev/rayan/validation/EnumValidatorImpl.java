package dev.rayan.validation;

import dev.rayan.enums.BaseEnum;
import dev.rayan.utils.StringToLowerUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumValidatorImpl implements ConstraintValidator<EnumValidator, String> {

    private List<String> acceptedValues;

    @Override
    public void initialize(EnumValidator annotation) {

        //BaseEnum is father in inheritance, getValue returns the Enum in string format
        final List<String> baseEnumList = Stream.of(annotation.enumClass().getEnumConstants())
                .map(BaseEnum::getValue)
                .collect(Collectors.toList());

        acceptedValues = StringToLowerUtils.toLower(baseEnumList);
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        if (value == null) return false;
        return acceptedValues.contains(value.toLowerCase());
    }
}

