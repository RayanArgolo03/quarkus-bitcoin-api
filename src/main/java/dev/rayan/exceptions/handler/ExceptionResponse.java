package dev.rayan.exceptions.handler;

import jakarta.validation.ConstraintViolation;
import jakarta.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class ExceptionResponse {

    //Exception attributes
    String title;
    int status;
    LocalDateTime timestamp = LocalDateTime.now();
    Set<ExceptionMessage> errors;

    //Exception message, inner record
    private record ExceptionMessage(String field, String message) {
    }

    //ConstraintViolations constructor
    public ExceptionResponse(Set<? extends ConstraintViolation<?>> violations) {
        this.title = BAD_REQUEST.name();
        this.status = BAD_REQUEST.getStatusCode();

        this.errors = violations.stream()
                .map(this::createExceptionMessage)
                .collect(Collectors.toSet());
    }

    //Simple exception constructor
    public ExceptionResponse(String message, Response.Status status) {
        this.title = status.name();
        this.status = status.getStatusCode();

        this.errors = Set.of(
                new ExceptionMessage(null, message)
        );
    }

    //Path example: sellBitcoins.request.totalQuantity
    private ExceptionMessage createExceptionMessage(final ConstraintViolation<?> violation) {

        final String[] fieldPath = violation.getPropertyPath()
                .toString()
                .split("\\.");

        final String field = fieldPath[fieldPath.length - 1];

        return new ExceptionMessage(field, violation.getMessage());
    }
}
