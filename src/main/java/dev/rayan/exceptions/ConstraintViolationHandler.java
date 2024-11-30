package dev.rayan.exceptions;

import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.util.Objects;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

@Provider
public final class ConstraintViolationHandler implements ExceptionMapper<ConstraintViolationException> {

    @Inject
    Logger log;

    @Override
    public Response toResponse(final ConstraintViolationException e) {

        log.errorf("Constraint violation bussiness exception!");

        return Response.status(BAD_REQUEST)
                .entity(new ExceptionResponse(e.getConstraintViolations()))
                .build();
    }


}
