package dev.rayan.dto.request;

import dev.rayan.model.client.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record UpdateClientRequest(

        @NotBlank(message = "Required first name!")
        String firstName,

        @NotBlank(message = "Required surname!")
        String surname,

        @Valid
        Address address

) {
}
