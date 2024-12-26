package dev.rayan.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CredentialRequest(
        @NotBlank(message = "Email required!")
        @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", flags = Pattern.Flag.CASE_INSENSITIVE,
                message = "Invalid email!")
        String email,

        @NotBlank(message = "Password required!")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{5,8}$", message = "Invalid password! Between 5 and 8 characters, at least 1 special character and a capital letter!")
        String password) {
}
