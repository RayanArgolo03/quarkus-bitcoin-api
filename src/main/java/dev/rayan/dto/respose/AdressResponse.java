package dev.rayan.dto.respose;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AdressResponse(

        String cep,
        String state,
        String neighbourhood,
        String street,

        @NotBlank(message = "Required house number!")
        @Min(value = 1, message = "House number can´t be less than 1!")
        Integer houseNumber
) {
}
