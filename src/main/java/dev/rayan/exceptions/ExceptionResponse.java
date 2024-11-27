package dev.rayan.exceptions;

import jakarta.validation.ConstraintViolation;
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
    private record ExceptionMessage(String field, String message) { }

    //Validator params constructor
    public ExceptionResponse(Set<? extends ConstraintViolation<?>> violations) {
        this.title = BAD_REQUEST.name();
        this.status = BAD_REQUEST.getStatusCode();

        this.errors = violations.stream()
                .map(v -> new ExceptionMessage(v.getPropertyPath().toString(), v.getMessage()))
                .collect(Collectors.toSet());
    }

    //Simple exception constructor
    public ExceptionResponse(String message) {
        this.title = INTERNAL_SERVER_ERROR.name();
        this.status = INTERNAL_SERVER_ERROR.getStatusCode();

        this.errors = Set.of(
                new ExceptionMessage(null, message)
        );
    }


}
