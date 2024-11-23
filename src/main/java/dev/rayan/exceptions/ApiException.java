package dev.rayan.exceptions;

import dev.rayan.dto.request.TransactionRequest;
import jakarta.validation.ConstraintViolation;
import lombok.Getter;

import java.io.Serial;
import java.util.Set;

@Getter
public final class ApiException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ApiException(String message) {
        super(message);
    }

}
