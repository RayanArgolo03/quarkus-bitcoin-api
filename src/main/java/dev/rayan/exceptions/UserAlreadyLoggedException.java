package dev.rayan.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.io.Serial;

public final class UserAlreadyLoggedException extends WebApplicationException {

    @Serial
    private static final long serialVersionUID = 1L;

    public UserAlreadyLoggedException(String message, Response.Status status) {
        super(message, status);
    }
}
