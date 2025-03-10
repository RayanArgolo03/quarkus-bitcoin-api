package dev.rayan.dto.request.authentication;

import jakarta.validation.Valid;

public record UpdatePasswordRequest(
        String currentPassword,
        @Valid
        UpdatePasswordRequest updatePassword) {
}
