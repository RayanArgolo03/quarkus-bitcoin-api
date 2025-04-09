package dev.rayan.dto.response.client;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ForgotPasswordResponse(
        String code,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime madeAt
) {
}
