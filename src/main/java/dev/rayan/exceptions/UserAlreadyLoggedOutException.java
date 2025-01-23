package dev.rayan.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.io.Serial;

public final class UserAlreadyLoggedOutException extends WebApplicationException {

    @Serial
    private static final long serialVersionUID = 1L;

    public UserAlreadyLoggedOutException(String message, Response.Status status) {
        super(message, status);
    }
}
