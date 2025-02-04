package dev.rayan.dto.response.client;

import dev.rayan.model.client.Address;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record ClientByCreatedAtResponse(
        String firstName,
        String surname,
        String birthDate,
        String cpf,
        String email,
        Address address) {
}
