package dev.rayan.dto.request.client;

import jakarta.validation.constraints.NotBlank;

public record UpdateClientRequest(

        @NotBlank(message = "Required first name!")
        String firstName,

        @NotBlank(message = "Required surname!")
        String surname) {

}
