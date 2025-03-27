package dev.rayan.dto.request.authentication;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.QueryParam;
import lombok.Getter;

@Getter
public final class ForgotPasswordRequest {

    @QueryParam("email")
    @NotBlank(message = "Required email!")
    @Email(message = "Invalid email! Pattern required: xxx@domain.com",
            regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}")
    String email;

    @QueryParam("code")
    @NotBlank(message = "Required code!")
    String code;
}
