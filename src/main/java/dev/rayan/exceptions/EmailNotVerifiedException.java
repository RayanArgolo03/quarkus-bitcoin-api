package dev.rayan.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.Getter;

import java.io.Serial;

public final class EmailNotVerifiedException extends WebApplicationException {

    @Serial
    private static final long serialVersionUID = 1L;

    public EmailNotVerifiedException(String message, Response.Status status) {
        super(message, status);
    }



}
