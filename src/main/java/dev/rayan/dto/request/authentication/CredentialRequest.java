package dev.rayan.dto.request.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CredentialRequest(

        @NotBlank(message = "Email required!")
        @Email(message = "Invalid email! Pattern required: xxx@domain.com",
                regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}")
        String email,

        @NotBlank(message = "Password required!")
        @Pattern(message = "Invalid password! Between 5 and 8 characters, at least 1 special character and a capital letter!",
                regexp = "^(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{5,8}$")
        String password
) {
}