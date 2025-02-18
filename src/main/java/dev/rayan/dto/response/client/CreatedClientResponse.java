package dev.rayan.dto.response.client;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.rayan.model.Address;
import dev.rayan.model.Credential;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


public record CreatedClientResponse(
        UUID id,
        String firstName,
        String surname,
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate birthDate,
        String cpf,
        Credential credential,
        Address address,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime createdAt) {
}
