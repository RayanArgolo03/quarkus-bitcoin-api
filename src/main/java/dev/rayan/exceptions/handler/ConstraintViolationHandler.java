package dev.rayan.exceptions.handler;

import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

@Provider
public final class ConstraintViolationHandler implements ExceptionMapper<ConstraintViolationException> {

    @Inject
    Logger log;

    @Override
    public Response toResponse(final ConstraintViolationException e) {

        log.errorf("Exceptions: %s", e.getMessage());
        return Response.status(BAD_REQUEST)
                .entity(new ExceptionResponse(e.getConstraintViolations()))
                .build();
    }


}
