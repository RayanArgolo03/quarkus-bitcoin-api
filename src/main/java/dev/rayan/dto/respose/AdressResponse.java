package dev.rayan.dto.respose;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AdressResponse(

        //Validate in AdressResource endpoint
        String cep,

        //Blocked edit by front-end
        @JsonProperty("estado")
        String state,
        @JsonProperty("bairro")
        String neighbourhood,
        @JsonProperty("logradouro")
        String street,

        @NotBlank(message = "Required house number!")
        @Min(value = 1, message = "House number can´t be less than 1!")
        Integer houseNumber
) {
}
