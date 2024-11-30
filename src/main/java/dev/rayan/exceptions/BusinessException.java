package dev.rayan.exceptions;

import lombok.Getter;

import java.io.Serial;

@Getter
public final class BusinessException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public BusinessException(String message) {super(message);}
}
