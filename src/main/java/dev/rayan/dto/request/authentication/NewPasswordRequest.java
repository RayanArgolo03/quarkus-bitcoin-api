package dev.rayan.dto.request.authentication;

import dev.rayan.validation.ConfirmedPasswordEquals;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@ConfirmedPasswordEquals(message = "Different passwords: confirmed password should be equals to password!")
public record NewPasswordRequest(

        @NotBlank(message = "Password required!")
        @Pattern(message = "Invalid password! Between 5 and 8 characters, at least 1 special character and a capital letter!",
                regexp = "^(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{5,8}$")
        String password,

        @NotBlank(message = "Password required!")
        @Pattern(message = "Invalid password! Between 5 and 8 characters, at least 1 special character and a capital letter!",
                regexp = "^(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{5,8}$")
        String confirmedPassword
) {
}