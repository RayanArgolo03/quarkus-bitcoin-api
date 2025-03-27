package dev.rayan.dto.response.client;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ClientResponse(
        CredentialResponse credential,
        String firstName,
        String surname,
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate birthDate,
        String cpf,
        //Formatted by clientMapper
        String completeAddress,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime createdAt) {
}
