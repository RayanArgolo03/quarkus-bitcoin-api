package dev.rayan.dto.request;

import dev.rayan.model.client.Address;
import dev.rayan.validation.OverEighteenYears;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

public record ClientRequest (

        @NotBlank(message = "Required first name!")
        @Pattern(regexp = "^(?=([A-ZÀ-ÝŐŰẞŒ]|([a-zß-ÿőűœ][ '])))(?=(?![a-zß-ÿőűœ]+[A-ZÀ-ÝŐŰẞŒ]))(?=(?!.*[A-ZÀ-ÝŐŰẞŒ][A-ZÀ-ÝŐŰẞŒ]))(?=(?!.*[- '][- ']))[A-ZÀ-ÝŐŰẞŒß-ÿőűœa-z- ']{2,}([a-zß-ÿőűœ]|(, Jr.))$\n")
        String firstName,

        @NotBlank(message = "Required surname!")
        @Pattern(regexp = "^(?=([A-ZÀ-ÝŐŰẞŒ]|([a-zß-ÿőűœ][ '])))(?=(?![a-zß-ÿőűœ]+[A-ZÀ-ÝŐŰẞŒ]))(?=(?!.*[A-ZÀ-ÝŐŰẞŒ][A-ZÀ-ÝŐŰẞŒ]))(?=(?!.*[- '][- ']))[A-ZÀ-ÝŐŰẞŒß-ÿőűœa-z- ']{2,}([a-zß-ÿőűœ]|(, Jr.))$\n")
        String surname,

        @NotNull(message = "Required birth date")
        @OverEighteenYears(message = "The age must not be less than 18 years old!")
        LocalDate birthDate,

        @NotBlank(message = "Required CPF!")
        @CPF(message = "Invalid CPF!")
        String cpf,

        @Valid
        Address address
) {
}
