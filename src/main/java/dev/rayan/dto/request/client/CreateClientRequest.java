package dev.rayan.dto.request.client;

import dev.rayan.model.Address;
import dev.rayan.validation.OverEighteenYears;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

public record CreateClientRequest(

        @NotBlank(message = "Required first name!")
        String firstName,

        @NotBlank(message = "Required surname!")
        String surname,

        @NotNull(message = "Required birth quotedAt")
        @OverEighteenYears(message = "The age must not be less than 18 years old!")
        LocalDate birthDate,

        @NotBlank(message = "Required CPF!")
        @CPF(message = "Invalid CPF!")
        String cpf,

        @Valid
        @NotNull(message = "Required address!")
        Address address

) {
}
