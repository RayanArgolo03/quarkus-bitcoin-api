package dev.rayan.exceptions;

import lombok.Getter;

import java.io.Serial;

@Getter
public final class ApiException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ApiException(String message) {
        super(message);
    }

}
