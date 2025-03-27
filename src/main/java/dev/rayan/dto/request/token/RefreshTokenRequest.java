package dev.rayan.dto.request.token;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "Invalid refresh token!")
        String refreshToken) {
}
