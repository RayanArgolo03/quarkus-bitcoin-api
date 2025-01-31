package dev.rayan.exceptions.handler;

import dev.rayan.exceptions.BusinessException;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

@Provider
public final class GeneralExceptionHandler implements ExceptionMapper<Exception> {

    @Inject
    Logger log;

    @Override
    public Response toResponse(final Exception e) {

        final Response.Status status = (e instanceof BusinessException)
                ? BAD_REQUEST
                : (e instanceof WebApplicationException ee)
                ? ee.getResponse().getStatusInfo().toEnum()
                : INTERNAL_SERVER_ERROR;

        log.errorf("Exception: %s, Message: %s", e.getClass().getSimpleName(), e.getMessage());
        return Response.status(status)
                .entity(new ExceptionResponse(e.getMessage(), status))
                .build();
    }


}
