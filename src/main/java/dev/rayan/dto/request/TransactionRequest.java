package dev.rayan.dto.request;

import dev.rayan.model.client.Client;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record TransactionRequest(

        @DecimalMin(value = "0.00001", message = "Minimum quantity required: 0.00001")
        float quantity,

        @NotNull(message = "Client can´t be null") Client client) {

}
