package dev.rayan.exceptions;

import lombok.Getter;

import java.io.Serial;

@Getter
public final class ApiException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "The server was unable to complete your request, contact @rayan_argolo";

    public ApiException() {
        super(MESSAGE);
    }


}
