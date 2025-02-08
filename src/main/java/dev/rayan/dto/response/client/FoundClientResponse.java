package dev.rayan.dto.response.client;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.rayan.model.Address;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.LocalDate;

@RegisterForReflection
public record FoundClientResponse(
        String firstName,
        String surname,
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate birthDate,
        String cpf,
        String email,
        Address address) {
}
