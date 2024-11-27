package dev.rayan.exceptions;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import static jakarta.ws.rs.core.Response.Status.*;

@Provider
public final class ExceptionHandler implements ExceptionMapper<Exception> {

    @Inject
    Logger log;

    @Override
    public Response toResponse(final Exception e) {

        if (e instanceof BusinessException ee) {
            log.errorf("Bussiness exception!");
            return Response.status(BAD_REQUEST)
                    .entity(new ExceptionResponse(ee.getViolations()))
                    .build();
        }

        log.errorf("Server error!");
        return Response.status(INTERNAL_SERVER_ERROR)
                .entity(new ExceptionResponse(e.getMessage()))
                .build();
    }


}
