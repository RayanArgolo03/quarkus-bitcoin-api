package dev.rayan.exceptions.handler;

import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.Set;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

public record ExceptionResponse(

        //Exception attributes
        String title,
        int status,
        LocalDateTime timestamp,
        Set<ErrorMessage> errors

) {

    //Error message attribute, inner record
    public record ErrorMessage(String field, String message) {
    }

    //Simple exception constructor
    public ExceptionResponse(String message, Response.Status status) {
        this(
                status.name(),
                status.getStatusCode(),
                LocalDateTime.now(),
                Set.of(new ErrorMessage(null, message))
        );
    }

    //ConstraintViolations constructor
    public ExceptionResponse(Set<ErrorMessage> errors) {
        this(
                BAD_REQUEST.name(),
                BAD_REQUEST.getStatusCode(),
                LocalDateTime.now(),
                errors
        );
    }


}
