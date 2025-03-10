package dev.rayan.dto.response.token;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record TokensResponse(
        String accessToken,
        String refreshToken,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime issuedAt,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime expiresIn) {

    public TokensResponse(String accessToken, String refreshToken, LocalDateTime expiresIn) {
        this(accessToken, refreshToken, LocalDateTime.now(), expiresIn);
    }
}
