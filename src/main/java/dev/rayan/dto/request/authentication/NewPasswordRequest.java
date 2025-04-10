package dev.rayan.dto.request.authentication;

import dev.rayan.validation.EqualsPassword;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@EqualsPassword(message = "Different passwords: confirmed password should be equals to new password!")
public record NewPasswordRequest(

        @NotNull(message = "New password required!")
        @Pattern(message = "Invalid password! Between 5 and 8 characters, at least 1 special character and a capital letter!",
                regexp = "^(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{5,8}$")
        String newPassword,

        @NotNull(message = "Confirmed new password required!")
        @Pattern(message = "Invalid password! Between 5 and 8 characters, at least 1 special character and a capital letter!",
                regexp = "^(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{5,8}$")
        String confirmedNewPassword
) {
}