package dev.rayan.exceptions;

import jakarta.ws.rs.WebApplicationException;

import java.io.Serial;

import static jakarta.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;

public final class ApiException extends WebApplicationException {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "The service is unavailable, contact the support in Linkedin @rayan_argolo";

    public ApiException() {
        super(MESSAGE, SERVICE_UNAVAILABLE);
    }


}
