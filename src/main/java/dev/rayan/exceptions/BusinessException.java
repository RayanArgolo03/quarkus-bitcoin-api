package dev.rayan.exceptions;

import jakarta.validation.ConstraintViolation;
import lombok.Getter;

import java.io.Serial;
import java.util.Set;

@Getter
public final class BusinessException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Set<? extends ConstraintViolation<?>> violations;

    public BusinessException(Set<? extends ConstraintViolation<?>> violations) {
        this.violations = violations;
    }
}
