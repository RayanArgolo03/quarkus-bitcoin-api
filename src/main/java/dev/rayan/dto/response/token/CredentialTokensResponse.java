package dev.rayan.dto.response.token;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record CredentialTokensResponse(
        String accessToken,
        String refreshToken,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime issuedAt,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime expiresIn) {

}
