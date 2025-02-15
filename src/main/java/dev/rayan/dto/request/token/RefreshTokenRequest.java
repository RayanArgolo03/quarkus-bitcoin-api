package dev.rayan.dto.request.token;

import jakarta.validation.constraints.NotNull;

public record RefreshTokenRequest(
        @NotNull(message = "Required refresh token!")
        String refreshToken) {
}
