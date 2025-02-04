package dev.rayan.dto.response.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.rayan.model.client.Address;
import dev.rayan.model.client.Credential;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.UUID;


public record CreatedClientResponse(
        UUID id,
        String firstName,
        String surname,
        String birthDate,
        String cpf,
        Credential credential,
        Address address,
        String createdAt,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        String updatedAt) {
}
