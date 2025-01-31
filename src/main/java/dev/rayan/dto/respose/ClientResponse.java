package dev.rayan.dto.respose;

import dev.rayan.model.client.Address;
import dev.rayan.model.client.Credential;

import java.util.UUID;

public record ClientResponse(UUID id,
                             String firstName,
                             String surname,
                             String birthDate,
                             String cpf,
                             Credential credential,
                             Address address,
                             String createdAt,
                             String updatedAt) {}
