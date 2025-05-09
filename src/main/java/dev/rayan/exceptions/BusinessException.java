package dev.rayan.exceptions;

import jakarta.ws.rs.WebApplicationException;

import java.io.Serial;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

public final class BusinessException extends WebApplicationException {

    @Serial
    private static final long serialVersionUID = 1L;

    public BusinessException(String message) {
        super(message, BAD_REQUEST);
    }
}
