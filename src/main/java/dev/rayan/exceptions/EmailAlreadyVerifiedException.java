package dev.rayan.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.Getter;

import java.io.Serial;

import static jakarta.ws.rs.core.Response.Status.GONE;

public final class EmailAlreadyVerifiedException extends WebApplicationException {

    @Serial
    private static final long serialVersionUID = 1L;

    public EmailAlreadyVerifiedException(String message, Response.Status status) {
        super(message, status);
    }

}
