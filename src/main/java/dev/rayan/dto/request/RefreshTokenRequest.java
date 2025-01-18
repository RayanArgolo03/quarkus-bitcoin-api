package dev.rayan.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "Required refresh token!") String refreshToken
) {}
