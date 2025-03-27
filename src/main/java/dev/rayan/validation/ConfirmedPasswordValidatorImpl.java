package dev.rayan.validation;

import dev.rayan.dto.request.authentication.NewPasswordRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class ConfirmedPasswordValidatorImpl implements ConstraintValidator<ConfirmedPasswordEquals, NewPasswordRequest> {

    @Override
    public boolean isValid(final NewPasswordRequest newPasswordRequest, final ConstraintValidatorContext context) {
        return Objects.equals(
                newPasswordRequest.newPassword(),
                newPasswordRequest.confirmedNewPassword()
        );
    }
}