package dev.rayan.exceptions;

import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.util.Objects;

import static jakarta.ws.rs.core.Response.Status.*;

@Provider
public final class GeneralExceptionHandler implements ExceptionMapper<Exception> {

    @Inject
    Logger log;

    @Override
    public Response toResponse(final Exception e) {

        final Response.Status status = (e instanceof BusinessException)
                ? BAD_REQUEST
                : (e instanceof NotFoundException)
                ? NOT_FOUND
                : INTERNAL_SERVER_ERROR;

        log.errorf("Exception! %s", e.getMessage());
        return Response.status(status)
                .entity(new ExceptionResponse(e.getMessage(), status))
                .build();
    }


}
