package dev.rayan.dto.request.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ForgotCredentialRequest(
        @NotBlank(message = "Email required!")
        @Email(message = "Invalid email! Pattern required: xxx@domain.com",
                regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}")
        String email
) {}