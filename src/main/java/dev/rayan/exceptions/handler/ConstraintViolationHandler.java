package dev.rayan.exceptions.handler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.util.Set;
import java.util.stream.Collectors;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

@Provider
public final class ConstraintViolationHandler implements ExceptionMapper<ConstraintViolationException> {

    private static final Logger LOG = Logger.getLogger(ConstraintViolationHandler.class);

    @Override
    public Response toResponse(final ConstraintViolationException e) {

        LOG.errorf("Exceptions: %s", e.getMessage());

        final Set<ExceptionResponse.ErrorMessage> errors = e.getConstraintViolations()
                .stream()
                .map(this::formatErrorMessage)
                .collect(Collectors.toSet());

        return Response.status(BAD_REQUEST)
                .entity(new ExceptionResponse(errors))
                .build();
    }


    private ExceptionResponse.ErrorMessage formatErrorMessage(final ConstraintViolation<?> violation) {

        //Path example: sellBitcoins.request.totalQuantity
        String[] fieldPath = violation.getPropertyPath()
                .toString()
                .split("\\.");

        String field = fieldPath[fieldPath.length - 1];
        String message = violation.getMessage();

        return new ExceptionResponse.ErrorMessage(field, message);
    }


}
