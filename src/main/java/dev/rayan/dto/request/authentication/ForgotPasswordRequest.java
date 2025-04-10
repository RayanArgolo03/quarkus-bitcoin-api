package dev.rayan.dto.request.authentication;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.QueryParam;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class ForgotPasswordRequest {

    @QueryParam("email")
    @NotNull(message = "Email required!")
    @Email(message = "Invalid email!",
            regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}")
    String email;

    @QueryParam("code")
    @NotBlank(message = "Required code!")
    String code;
}
