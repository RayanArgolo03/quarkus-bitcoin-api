package dev.rayan.dto.request.authentication;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
public record UpdatePasswordRequest(

        @NotBlank(message = "Password required!")
        @Pattern(message = "Invalid password! Between 5 and 8 characters, at least 1 special character and a capital letter!",
                regexp = "^(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{5,8}$")
        String currentPassword,

        @NotNull(message = "Required new passwords!")
        @JsonUnwrapped
        @Valid
        NewPasswordRequest newPasswordRequest) {
}
